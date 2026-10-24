package iguanaman.iguanatweaks.weight;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

final class OverrideWeightResolver implements IWeightResolver {
    enum Mode { ITEM, ORE_DICTIONARY, MOD_DEFAULT }

    private final WeightOverrides overrides;
    private final Mode mode;

    OverrideWeightResolver(WeightOverrides overrides, Mode mode) {
        this.overrides = overrides;
        this.mode = mode;
    }

    @Override
    public WeightResult resolve(ItemStack stack, WeightContext context) {
        Double value = null;
        String source = null;
        if(mode == Mode.ITEM) {
            value = overrides.item(stack);
            source = "item-override";
        } else if(mode == Mode.ORE_DICTIONARY) {
            int[] ids = OreDictionary.getOreIDs(stack);
            for(int id : ids) {
                String name = OreDictionary.getOreName(id);
                value = overrides.ore(name);
                if(value != null) { source = "ore-override:" + name; break; }
            }
        } else {
            value = overrides.mod(stack);
            source = "mod-default";
        }
        return value == null ? WeightResult.unresolved() : WeightResult.resolved(value.doubleValue(), source, CachePolicy.STATIC_ITEM_META);
    }
}
