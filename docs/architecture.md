# Encumbrance replacement architecture

The first development goal is a behavior-compatible replacement for IguanaTweaks' weight, terrain movement, armor, and stack-size features.

Weight resolution is ordered from most explicit to least explicit:

1. Exact NBT override.
2. Exact item and metadata override.
3. Exact item override.
4. Ore-dictionary override.
5. Per-mod override.
6. Registered compatibility provider, including GT6 material data.
7. Automatic ore-dictionary and recipe inference.
8. Legacy IguanaTweaks material fallback.

Resolvers return the intrinsic weight of one item. Stack count, worn-armor reduction, and container retention are applied afterwards.

Portable inventories are resolved from the `ItemStack` through registered adapters. Placed inventories belong to tile entities and are not treated as player-carried contents. Recursive container inspection is bounded and cached according to each result's `CachePolicy`.

Initial compatibility rules:

- Items in the player inventory count at full weight.
- Worn armor counts at a configurable reduced percentage.
- Ordinary portable containers add 100 percent of contained weight.
- Explicit magical-container rules may reduce contained weight.
- Opening a chest or machine must not add that external inventory to player burden.
- Legacy terrain and stack-size behavior remains available while enhanced modes are developed and validated.
