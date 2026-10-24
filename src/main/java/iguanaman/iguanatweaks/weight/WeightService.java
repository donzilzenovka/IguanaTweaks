package iguanaman.iguanatweaks.weight;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.config.IguanaWeightsConfig;

/** Central weight entry point. Static item/meta results are cached; NBT stacks are not. */
public final class WeightService {
    private static final Map<StaticWeightKey, Double> STATIC_WEIGHTS = new HashMap<StaticWeightKey, Double>();

    private WeightService() {}

    public static double getUnitWeight(ItemStack stack, WeightContext context) {
        if(stack == null || stack.getItem() == null) return 0d;

        boolean cacheable = !stack.hasTagCompound();
        StaticWeightKey key = cacheable ? new StaticWeightKey(stack.getItem(), stack.getItem().getDamage(stack)) : null;
        if(cacheable) {
            Double cached = STATIC_WEIGHTS.get(key);
            if(cached != null) return cached.doubleValue();
        }

        double weight = IguanaWeightsConfig.getWeight(stack);
        if(cacheable) STATIC_WEIGHTS.put(key, Double.valueOf(weight));
        return weight;
    }

    public static void clearCaches() {
        STATIC_WEIGHTS.clear();
    }

    private static final class StaticWeightKey {
        private final Item item;
        private final int metadata;

        private StaticWeightKey(Item item, int metadata) {
            this.item = item;
            this.metadata = metadata;
        }

        @Override
        public boolean equals(Object other) {
            if(this == other) return true;
            if(!(other instanceof StaticWeightKey)) return false;
            StaticWeightKey that = (StaticWeightKey)other;
            return item == that.item && metadata == that.metadata;
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(item) + metadata;
        }
    }
}
