package aar226_akc55_ayc62_ahl88.cfg.optimizations;

public enum OptimizationType {
    CONSTANT_FOLDING {
        @Override
        public String toString() {
            return "cf";
        }
    },
//    DEAD_CODE_ELIMINATION {
//        @Override
//        public String toString() {
//            return "dce";
//        }
//    },

    INLINING {
        @Override
        public String toString() {
            return "inl";
        }
    },
}