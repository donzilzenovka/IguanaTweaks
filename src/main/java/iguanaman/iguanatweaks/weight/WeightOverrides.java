package iguanaman.iguanatweaks.weight;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import iguanaman.iguanatweaks.IguanaTweaks;

/** Per-mod JSON overrides loaded from config/IguanaTweaks/weights. */
final class WeightOverrides {
    private final Map<String, Double> items = new HashMap<String, Double>();
    private final Map<String, Double> oreDictionary = new HashMap<String, Double>();
    private final Map<String, Double> modDefaults = new HashMap<String, Double>();

    static WeightOverrides load(File folder) {
        WeightOverrides result = new WeightOverrides();
        if(!folder.exists() && !folder.mkdirs()) {
            IguanaTweaks.log.warn("Could not create weight override folder " + folder);
            return result;
        }
        File[] files = folder.listFiles();
        if(files == null) return result;
        for(File file : files) {
            if(file.isFile() && file.getName().toLowerCase().endsWith(".json")) result.read(file);
        }
        return result;
    }

    private void read(File file) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            String fileMod = file.getName().substring(0, file.getName().length() - 5);
            String mod = root.has("mod") ? root.get("mod").getAsString() : fileMod;
            if(root.has("defaultWeight")) modDefaults.put(mod, checked(root.get("defaultWeight"), file));
            readMap(root, "items", items, mod + ":", file);
            readMap(root, "oreDictionary", oreDictionary, "", file);
            IguanaTweaks.log.info("Loaded weight overrides from " + file.getName());
        } catch(Exception e) {
            IguanaTweaks.log.error("Could not read weight overrides from " + file, e);
        } finally {
            if(reader != null) try { reader.close(); } catch(Exception ignored) {}
        }
    }

    private static void readMap(JsonObject root, String member, Map<String, Double> target, String itemPrefix, File file) {
        if(!root.has(member)) return;
        for(Map.Entry<String, JsonElement> entry : root.getAsJsonObject(member).entrySet()) {
            String key = entry.getKey();
            if(member.equals("items") && key.indexOf(':') < 0) key = itemPrefix + key;
            target.put(key, checked(entry.getValue(), file));
        }
    }

    private static Double checked(JsonElement element, File file) {
        double value = element.getAsDouble();
        if(Double.isNaN(value) || Double.isInfinite(value) || value < 0d) throw new IllegalArgumentException("Invalid weight in " + file);
        return Double.valueOf(value);
    }

    Double item(ItemStack stack) {
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        if(name == null) return null;
        Double exact = items.get(name + "@" + stack.getItem().getDamage(stack));
        return exact == null ? items.get(name) : exact;
    }

    Double ore(String name) { return oreDictionary.get(name); }

    Double mod(ItemStack stack) {
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        if(name == null) return null;
        int colon = name.indexOf(':');
        return colon < 0 ? null : modDefaults.get(name.substring(0, colon));
    }
}
