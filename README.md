Iguana Tweaks
=============

The Home of this Minecraft Mod, originally created by iguana_man.

This fork is being developed as a behavior-compatible Minecraft 1.7.10 encumbrance replacement. The first milestone preserves IguanaTweaks terrain movement and stack-size features while introducing metadata-aware, NBT-aware, material-aware, and recursively contained inventory weights.

See [docs/architecture.md](docs/architecture.md) for the current design boundary.

Prototype 2 uses Gradle 8.14.3 with the RetroFuturaGradle-based GTNH convention
toolchain while continuing to produce Java 8-compatible Minecraft 1.7.10 code.

Prototype 1 corrected player inventory enumeration, counts worn armour at a configurable
percentage of its carried weight, and introduces a metadata-aware intrinsic-weight cache.
Terrain movement and legacy stack-size restrictions remain active. Recursive portable
inventory handling remains a subsequent milestone.

Prototype 3 adds an ordered resolver pipeline, automatic Ore Dictionary shape ratios,
per-mod JSON overrides, and an optional reflection-isolated GregTech 6 material adapter.
When GT6 is absent the adapter is not created and resolution continues through the Ore
Dictionary and original IguanaTweaks material defaults.

Per-mod files live in `config/IguanaTweaks/weights/<modid>.json`:

```json
{
  "mod": "examplemod",
  "defaultWeight": 0.0625,
  "items": {
    "special_item": 0.5,
    "special_item@3": 0.75,
    "othermod:shared_item": 0.25
  },
  "oreDictionary": {
    "ingotCopper": 0.18
  }
}
```

All members are optional. Exact metadata items override exact items, followed by Ore
Dictionary overrides and the per-mod default. Existing `weights.json` entries retain
highest priority for compatibility.

Prototype 5 makes crafted, unstackable or already-NBT-bearing outputs conserve the net weight of the
actual recipe inputs. One item from each occupied crafting slot is counted, returned
container items are subtracted, and the result is divided by the recipe output count.
The recipe-derived value is stored in the output NBT, allowing otherwise identical GT6
tools made from different materials to retain different weights. Exact item overrides
still take priority. Qualifying recipes are decorated during post-initialization so the
weighted NBT is present in both the crafting-result preview and the collected item. The
pickup event remains as a fallback. This behavior can be disabled with
`conserveCraftingWeight=false`.
