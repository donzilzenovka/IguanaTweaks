package iguanaman.iguanatweaks.weight;

/** Declares how safely a resolved unit weight may be cached. */
public enum CachePolicy {
    STATIC_ITEM_META,
    NBT_SENSITIVE,
    CONTEXT_SENSITIVE,
    UNCACHEABLE
}
