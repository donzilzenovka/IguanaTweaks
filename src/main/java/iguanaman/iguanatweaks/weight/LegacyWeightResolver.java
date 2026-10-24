package iguanaman.iguanatweaks.weight;

import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.config.IguanaWeightsConfig;

final class LegacyWeightResolver implements IWeightResolver {
    @Override
    public WeightResult resolve(ItemStack stack, WeightContext context) {
        return WeightResult.resolved(IguanaWeightsConfig.getDefaultWeight(stack), "legacy-material-default", CachePolicy.STATIC_ITEM_META);
    }
}
