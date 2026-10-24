package iguanaman.iguanatweaks.weight;

import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.config.IguanaWeightsConfig;

final class LegacyJsonWeightResolver implements IWeightResolver {
    @Override
    public WeightResult resolve(ItemStack stack, WeightContext context) {
        Double weight = IguanaWeightsConfig.getConfiguredWeight(stack);
        return weight == null ? WeightResult.unresolved() : WeightResult.resolved(weight.doubleValue(), "legacy-weights.json", CachePolicy.STATIC_ITEM_META);
    }
}
