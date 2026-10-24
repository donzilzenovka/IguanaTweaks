package iguanaman.iguanatweaks.weight;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.config.IguanaWeightsConfig;

/** Ordered weight resolver with conservative caching. */
public final class WeightService {
    private static final List<IWeightResolver> RESOLVERS = new ArrayList<IWeightResolver>();
    private static final Map<StaticWeightKey, WeightResult> STATIC_WEIGHTS = new HashMap<StaticWeightKey, WeightResult>();
    private static boolean initialized;

    private WeightService() {}

    public static void initialize(File configFolder) {
        RESOLVERS.clear();
        WeightOverrides overrides = WeightOverrides.load(new File(configFolder, "weights"));
        RESOLVERS.add(new LegacyJsonWeightResolver());
        RESOLVERS.add(new OverrideWeightResolver(overrides, OverrideWeightResolver.Mode.ITEM));
        RESOLVERS.add(new OverrideWeightResolver(overrides, OverrideWeightResolver.Mode.ORE_DICTIONARY));
        RESOLVERS.add(new OverrideWeightResolver(overrides, OverrideWeightResolver.Mode.MOD_DEFAULT));
        IWeightResolver gregTech = GregTech6WeightResolver.createIfAvailable();
        if(gregTech != null) RESOLVERS.add(gregTech);
        RESOLVERS.add(new OreDictionaryWeightResolver());
        RESOLVERS.add(new LegacyWeightResolver());
        clearCaches();
        initialized = true;
        IguanaTweaks.log.info("Weight resolver pipeline initialized with " + RESOLVERS.size() + " stages");
    }

    public static WeightResult resolve(ItemStack stack, WeightContext context) {
        if(stack == null || stack.getItem() == null) return WeightResult.resolved(0d, "empty", CachePolicy.UNCACHEABLE);
        if(!initialized) return WeightResult.resolved(IguanaWeightsConfig.getWeight(stack), "legacy-uninitialized", CachePolicy.STATIC_ITEM_META);
        boolean cacheable = !stack.hasTagCompound();
        StaticWeightKey key = cacheable ? new StaticWeightKey(stack.getItem(), stack.getItem().getDamage(stack), context.getCarryContext()) : null;
        if(cacheable) {
            WeightResult cached = STATIC_WEIGHTS.get(key);
            if(cached != null) return cached;
        }
        for(IWeightResolver resolver : RESOLVERS) {
            WeightResult result = resolver.resolve(stack, context);
            if(result != null && result.isResolved()) {
                if(cacheable && result.getCachePolicy() == CachePolicy.STATIC_ITEM_META) STATIC_WEIGHTS.put(key, result);
                return result;
            }
        }
        return WeightResult.unresolved();
    }

    public static double getUnitWeight(ItemStack stack, WeightContext context) {
        WeightResult result = resolve(stack, context);
        return result.isResolved() ? result.getUnitWeight() : 0d;
    }

    public static void clearCaches() {
        STATIC_WEIGHTS.clear();
    }

    private static final class StaticWeightKey {
        private final Item item;
        private final int metadata;
        private final CarryContext context;

        private StaticWeightKey(Item item, int metadata, CarryContext context) {
            this.item = item;
            this.metadata = metadata;
            this.context = context;
        }

        @Override
        public boolean equals(Object other) {
            if(this == other) return true;
            if(!(other instanceof StaticWeightKey)) return false;
            StaticWeightKey that = (StaticWeightKey)other;
            return item == that.item && metadata == that.metadata && context == that.context;
        }

        @Override
        public int hashCode() {
            return 31 * (31 * System.identityHashCode(item) + metadata) + context.ordinal();
        }
    }
}
