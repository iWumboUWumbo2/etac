package aar226_akc55_ayc62_ahl88.cfg.optimizations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Optimizations {
    private boolean[] optimizations;

    public Optimizations() {
        optimizations = new boolean[OptimizationType.values().length];
    }

    public void setOptimizations(OptimizationType... opts) {
        for (OptimizationType opt : opts) {
            optimizations[opt.ordinal()] = true;
        }
    }

    public void clearOptimizations(OptimizationType... opts) {
        for (OptimizationType opt : opts) {
            optimizations[opt.ordinal()] = false;
        }
    }

    public void setAll() {
        Arrays.fill(optimizations, true);
    }

    public void clearAll() {
        Arrays.fill(optimizations, false);
    }

    @Override
    public String toString() {
        return Arrays.toString(OptimizationType.values()) + "\n" + Arrays.toString(optimizations);
    }

    public boolean allClear(){
        for (Boolean opt : optimizations){
            if (opt){
                return false;
            }
        }
        return true;
    }
    public boolean isSet(OptimizationType opt) {
        return optimizations[opt.ordinal()];
    }

    public void reportOpts() {
        for (OptimizationType c : OptimizationType.values()) {
            System.out.println(c);
        }
    }
}
