package iguanaman.iguanatweaks.weight;

/** The intrinsic weight of one item and its diagnostic provenance. */
public final class WeightResult {
    private static final WeightResult UNRESOLVED = new WeightResult(false, 0.0D, "unresolved", CachePolicy.UNCACHEABLE, false);

    private final boolean resolved;
    private final double unitWeight;
    private final String source;
    private final CachePolicy cachePolicy;
    private final boolean includesContents;

    private WeightResult(boolean resolved, double unitWeight, String source, CachePolicy cachePolicy, boolean includesContents) {
        if(source == null) throw new IllegalArgumentException("source cannot be null");
        if(cachePolicy == null) throw new IllegalArgumentException("cachePolicy cannot be null");
        if(Double.isNaN(unitWeight) || Double.isInfinite(unitWeight) || unitWeight < 0.0D) {
            throw new IllegalArgumentException("unitWeight must be finite and non-negative");
        }
        this.resolved = resolved;
        this.unitWeight = unitWeight;
        this.source = source;
        this.cachePolicy = cachePolicy;
        this.includesContents = includesContents;
    }

    public static WeightResult unresolved() {
        return UNRESOLVED;
    }

    public static WeightResult resolved(double unitWeight, String source, CachePolicy cachePolicy) {
        return new WeightResult(true, unitWeight, source, cachePolicy, false);
    }

    public static WeightResult resolvedWithContents(double unitWeight, String source, CachePolicy cachePolicy) {
        return new WeightResult(true, unitWeight, source, cachePolicy, true);
    }

    public boolean isResolved() { return resolved; }
    public double getUnitWeight() { return unitWeight; }
    public String getSource() { return source; }
    public CachePolicy getCachePolicy() { return cachePolicy; }
    public boolean includesContents() { return includesContents; }
}
