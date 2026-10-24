package iguanaman.iguanatweaks.weight;

/** Bounded state passed through recursive container resolution. */
public final class WeightContext {
    public static final int DEFAULT_MAXIMUM_DEPTH = 8;

    private final CarryContext carryContext;
    private final int depth;
    private final int maximumDepth;

    public WeightContext(CarryContext carryContext) {
        this(carryContext, 0, DEFAULT_MAXIMUM_DEPTH);
    }

    public WeightContext(CarryContext carryContext, int maximumDepth) {
        this(carryContext, 0, maximumDepth);
    }

    private WeightContext(CarryContext carryContext, int depth, int maximumDepth) {
        if(carryContext == null) throw new IllegalArgumentException("carryContext cannot be null");
        if(depth < 0) throw new IllegalArgumentException("depth cannot be negative");
        if(maximumDepth < 0) throw new IllegalArgumentException("maximumDepth cannot be negative");
        this.carryContext = carryContext;
        this.depth = depth;
        this.maximumDepth = maximumDepth;
    }

    public CarryContext getCarryContext() { return carryContext; }
    public int getDepth() { return depth; }
    public int getMaximumDepth() { return maximumDepth; }
    public boolean canDescend() { return depth < maximumDepth; }

    public WeightContext forContainedItem() {
        if(!canDescend()) throw new IllegalStateException("maximum container nesting depth reached");
        return new WeightContext(CarryContext.CONTAINER_CONTENT, depth + 1, maximumDepth);
    }
}
