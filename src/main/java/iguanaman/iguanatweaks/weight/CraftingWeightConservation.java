package iguanaman.iguanatweaks.weight;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.config.IguanaConfig;

/** Applies net recipe-input weight while a crafting result is being created. */
public final class CraftingWeightConservation {
    private CraftingWeightConservation() {}

    @SuppressWarnings("unchecked")
    public static void install() {
        if(!IguanaConfig.conserveCraftingWeight) return;
        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        int decorated = 0;
        for(int index = 0; index < recipes.size(); ++index) {
            IRecipe recipe = recipes.get(index);
            if(recipe instanceof ConservingRecipe || !hasSafeOutput(recipe)) continue;
            recipes.set(index, new ConservingRecipe(recipe));
            ++decorated;
        }
        IguanaTweaks.log.info("Crafting weight conservation enabled for " + decorated + " unstackable or NBT-bearing recipes");
    }

    private static boolean hasSafeOutput(IRecipe recipe) {
        ItemStack output = recipe == null ? null : recipe.getRecipeOutput();
        return output != null && (output.getMaxStackSize() <= 1 || output.hasTagCompound());
    }

    public static void apply(ItemStack output, IInventory craftMatrix) {
        if(!IguanaConfig.conserveCraftingWeight || output == null || output.getItem() == null || craftMatrix == null) return;
        if(output.getMaxStackSize() > 1 && !output.hasTagCompound()) return;
        double inputWeight = getConsumedInputWeight(craftMatrix);
        if(inputWeight <= 0D) return;
        WeightService.recordCraftedWeight(output, inputWeight / Math.max(1, output.stackSize));
    }

    private static double getConsumedInputWeight(IInventory craftMatrix) {
        double total = 0D;
        WeightContext context = new WeightContext(CarryContext.INVENTORY);
        for(int slot = 0; slot < craftMatrix.getSizeInventory(); ++slot) {
            ItemStack ingredient = craftMatrix.getStackInSlot(slot);
            if(ingredient == null || ingredient.getItem() == null) continue;
            ItemStack single = ingredient.copy();
            single.stackSize = 1;
            total += WeightService.getUnitWeight(single, context);
            if(single.getItem().hasContainerItem(single)) {
                ItemStack returned = single.getItem().getContainerItem(single);
                if(returned != null) total -= WeightService.getUnitWeight(returned, context);
            }
        }
        return Math.max(0D, total);
    }

    private static final class ConservingRecipe implements IRecipe {
        private final IRecipe delegate;

        private ConservingRecipe(IRecipe delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean matches(InventoryCrafting inventory, World world) {
            return delegate.matches(inventory, world);
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inventory) {
            ItemStack result = delegate.getCraftingResult(inventory);
            apply(result, inventory);
            return result;
        }

        @Override
        public int getRecipeSize() {
            return delegate.getRecipeSize();
        }

        @Override
        public ItemStack getRecipeOutput() {
            return delegate.getRecipeOutput();
        }
    }
}
