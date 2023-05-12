package aar226_akc55_ayc62_ahl88.cfg.optimizations;

public enum OptimizationType {
    CONSTANT_FOLDING {
        @Override
        public String toString() {
            return "cf";
        }
    },
    DEAD_CODE_ELIMINATION {
        @Override
        public String toString() {
            return "dce";
        }
    },

    INLINING {
        @Override
        public String toString() {
            return "inl";
        }
    },
    CONSTPROP {
        @Override
        public String toString() {
            return "cp";
        }
    },

    COPYPROP {
        @Override
        public String toString() {
            return "copy";
        }
    },
    LICM {
        @Override
        public String toString() {
            return "licm";
        }
    },

    REGALLOC {
        @Override
        public String toString() {
            return "reg";
        }
    }
}
