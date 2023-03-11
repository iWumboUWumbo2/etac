package aar226_akc55_ayc62_ahl88;

import java.util.Arrays;

enum OptimizationTypes {
    CONSTANT_FOLDING,
    IR_LOWERING
}

public class Optimizations {
    private boolean[] optimizations;

    public Optimizations() {
        optimizations = new boolean[OptimizationTypes.values().length];
    }

    public void setOptimizations(OptimizationTypes... opts) {
        for (OptimizationTypes opt : opts) {
            optimizations[opt.ordinal()] = true;
        }
    }

    public void clearOptimizations(OptimizationTypes... opts) {
        for (OptimizationTypes opt : opts) {
            optimizations[opt.ordinal()] = false;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(OptimizationTypes.values()) + "\n" + Arrays.toString(optimizations);
    }

    public boolean isSet(OptimizationTypes opt) {
        return optimizations[opt.ordinal()];
    }
}
