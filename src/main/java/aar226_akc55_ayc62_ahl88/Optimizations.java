package aar226_akc55_ayc62_ahl88;

import java.util.Arrays;

enum OptimizationTypes {
    CONSTANT_FOLDING,
    IR_LOWERING
}

public class Optimizations {
    private Boolean[] optimizations;

    public Optimizations() {
        optimizations =
                (Boolean[]) Arrays.stream(OptimizationTypes.values())
                                  .map(e -> false)
                                  .toArray();
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

    public boolean isSet(OptimizationTypes opt) {
        return optimizations[opt.ordinal()];
    }
}
