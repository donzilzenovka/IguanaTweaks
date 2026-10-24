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
inventory handling, per-mod overrides, and GregTech material inference are subsequent
milestones and are not enabled in this prototype.
