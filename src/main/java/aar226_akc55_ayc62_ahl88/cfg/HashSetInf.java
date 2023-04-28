package aar226_akc55_ayc62_ahl88.cfg;

import java.util.*;

public class HashSetInf<E> extends HashSet<E> {

    private boolean infSize;

    public HashSetInf(){
        super();
        infSize = false;
    }
    public HashSetInf(boolean infiniteSet){
        super();
        infSize = infiniteSet;
    }
    public HashSetInf(Collection<? extends E> c) {
        super(c);
        infSize = false;
    }
//    public HashSetInf(int initialCapacity, float loadFactor) {
//        super(initialCapacity,loadFactor);
//        infSize = true;
//    }
//
//    public HashSetInf(int initialCapacity) {
//        super(initialCapacity);
//        infSize = true;
//    }



    public boolean isInfSize() {
        return infSize;
    }

    public void setInfSize(boolean infSize) {
        this.infSize = infSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HashSetInf<?> that = (HashSetInf<?>) o;
        return isInfSize() == that.isInfSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isInfSize());
    }
}
