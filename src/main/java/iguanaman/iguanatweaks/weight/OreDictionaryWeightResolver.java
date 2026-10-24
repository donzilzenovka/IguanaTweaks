package iguanaman.iguanatweaks.weight;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import iguanaman.iguanatweaks.config.IguanaConfig;

/** Cross-mod shape inference. Material-specific ratios are supplied by GT6 or overrides. */
final class OreDictionaryWeightResolver implements IWeightResolver {
    private static final Map<String, Double> PREFIX_AMOUNTS = new LinkedHashMap<String, Double>();
    static {
        PREFIX_AMOUNTS.put("block", 9d);
        PREFIX_AMOUNTS.put("plateDense", 9d);
        PREFIX_AMOUNTS.put("gear", 4d);
        PREFIX_AMOUNTS.put("plateDouble", 2d);
        PREFIX_AMOUNTS.put("dustSmall", 0.25d);
        PREFIX_AMOUNTS.put("dustTiny", 1d / 9d);
        PREFIX_AMOUNTS.put("rodLong", 1d);
        PREFIX_AMOUNTS.put("stickLong", 1d);
        PREFIX_AMOUNTS.put("ingot", 1d);
        PREFIX_AMOUNTS.put("dust", 1d);
        PREFIX_AMOUNTS.put("gem", 1d);
        PREFIX_AMOUNTS.put("plate", 1d);
        PREFIX_AMOUNTS.put("rod", 0.5d);
        PREFIX_AMOUNTS.put("stick", 0.5d);
        PREFIX_AMOUNTS.put("bolt", 0.125d);
        PREFIX_AMOUNTS.put("nugget", 1d / 9d);
    }

    @Override
    public WeightResult resolve(ItemStack stack, WeightContext context) {
        int[] ids = OreDictionary.getOreIDs(stack);
        for(int id : ids) {
            String name = OreDictionary.getOreName(id);
            for(Map.Entry<String, Double> entry : PREFIX_AMOUNTS.entrySet()) {
                if(name.startsWith(entry.getKey()) && name.length() > entry.getKey().length()) {
                    return WeightResult.resolved(IguanaConfig.oreDictionaryBaseWeight * entry.getValue().doubleValue(), "ore-dictionary:" + name, CachePolicy.STATIC_ITEM_META);
                }
            }
        }
        return WeightResult.unresolved();
    }
}
