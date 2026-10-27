package iguanaman.iguanatweaks.weight;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.config.IguanaConfig;

/** Applies net ingredient weight to crafted items via ItemCraftedEvent without changing recipes. */
public final class CraftingWeightConservation {
    private CraftingWeightConservation() {}

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

}
