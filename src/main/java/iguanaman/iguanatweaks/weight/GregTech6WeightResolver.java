package iguanaman.iguanatweaks.weight;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.config.IguanaConfig;

/** Optional GT6 bridge. No GregTech type occurs in this class's public contract. */
final class GregTech6WeightResolver implements IWeightResolver {
    private final Method weightMethod;
    private boolean failed;

    private GregTech6WeightResolver(Method weightMethod) {
        this.weightMethod = weightMethod;
    }

    static IWeightResolver createIfAvailable() {
        try {
            Class<?> om = Class.forName("gregapi.util.OM", false, GregTech6WeightResolver.class.getClassLoader());
            Method weight = om.getMethod("weight", ItemStack.class);
            IguanaTweaks.log.info("GregTech 6 material weight integration enabled");
            return new GregTech6WeightResolver(weight);
        } catch(ClassNotFoundException absent) {
            IguanaTweaks.log.info("GregTech 6 not present; using Ore Dictionary and legacy weight resolvers");
            return null;
        } catch(Exception incompatible) {
            IguanaTweaks.log.warn("GregTech API was found but its weight method is unavailable; integration disabled", incompatible);
            return null;
        }
    }

    @Override
    public WeightResult resolve(ItemStack stack, WeightContext context) {
        if(failed) return WeightResult.unresolved();
        try {
            ItemStack single = stack.copy();
            single.stackSize = 1;
            double kilograms = ((Number)weightMethod.invoke(null, single)).doubleValue();
            if(kilograms <= 0d || Double.isNaN(kilograms) || Double.isInfinite(kilograms)) return WeightResult.unresolved();
            return WeightResult.resolved(kilograms / IguanaConfig.gt6KilogramsPerWeightUnit, "gregtech6-material", CachePolicy.UNCACHEABLE);
        } catch(Exception error) {
            failed = true;
            IguanaTweaks.log.warn("GregTech weight integration failed and has been disabled for this session", error);
            return WeightResult.unresolved();
        }
    }
}
