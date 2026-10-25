package iguanaman.iguanatweaks.weight;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** Resolves recipe-specific weight recorded when an item is crafted. */
final class CraftedWeightResolver implements IWeightResolver {
    private static final String ROOT_KEY = "IguanaTweaks";
    private static final String WEIGHT_KEY = "CraftedUnitWeight";
    private static final String VERSION_KEY = "CraftedWeightVersion";
    private static final byte VERSION = 1;

    @Override
    public WeightResult resolve(ItemStack stack, WeightContext context) {
        NBTTagCompound root = stack.getTagCompound();
        if(root == null || !root.hasKey(ROOT_KEY, 10)) return WeightResult.unresolved();
        NBTTagCompound data = root.getCompoundTag(ROOT_KEY);
        if(data.getByte(VERSION_KEY) != VERSION || !data.hasKey(WEIGHT_KEY, 99)) return WeightResult.unresolved();
        double value = data.getDouble(WEIGHT_KEY);
        if(value < 0D || Double.isNaN(value) || Double.isInfinite(value)) return WeightResult.unresolved();
        return WeightResult.resolved(value, "crafted-input-conservation", CachePolicy.UNCACHEABLE);
    }

    static void write(ItemStack stack, double unitWeight) {
        if(stack == null || unitWeight < 0D || Double.isNaN(unitWeight) || Double.isInfinite(unitWeight)) return;
        NBTTagCompound root = stack.getTagCompound();
        if(root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        NBTTagCompound data = root.hasKey(ROOT_KEY, 10) ? root.getCompoundTag(ROOT_KEY) : new NBTTagCompound();
        data.setByte(VERSION_KEY, VERSION);
        data.setDouble(WEIGHT_KEY, unitWeight);
        root.setTag(ROOT_KEY, data);
    }
}
