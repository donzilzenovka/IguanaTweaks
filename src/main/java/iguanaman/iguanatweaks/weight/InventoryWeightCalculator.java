package iguanaman.iguanatweaks.weight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.config.IguanaConfig;

/** Calculates weight from player-owned slots without counting an opened container. */
public final class InventoryWeightCalculator {
    private InventoryWeightCalculator() {}

    public static double getCarriedWeight(EntityPlayer player) {
        if(player == null) return 0d;

        double total = sum(player.inventory.mainInventory, CarryContext.INVENTORY, 1d);
        double wornMultiplier = IguanaConfig.wornArmorWeightPercentage / 100d;
        total += sum(player.inventory.armorInventory, CarryContext.WORN_ARMOR, wornMultiplier);
        return total;
    }

    private static double sum(ItemStack[] stacks, CarryContext carryContext, double multiplier) {
        double total = 0d;
        WeightContext context = new WeightContext(carryContext);
        for(ItemStack stack : stacks) {
            if(stack == null || stack.stackSize <= 0) continue;
            double unitWeight = WeightService.getUnitWeight(stack, context);
            if(stack.getItem() instanceof ItemBlock) unitWeight *= IguanaConfig.rockWeight;
            total += unitWeight * (double)stack.stackSize * multiplier;
        }
        return total;
    }
}
