package iguanaman.iguanatweaks.weight;

import net.minecraft.item.ItemStack;

/** A single step in the ordered weight-resolution pipeline. */
public interface IWeightResolver {
    WeightResult resolve(ItemStack stack, WeightContext context);
}
