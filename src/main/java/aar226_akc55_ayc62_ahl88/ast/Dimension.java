package aar226_akc55_ayc62_ahl88.ast;

import java.util.ArrayList;
import java.util.Collections;

public class Dimension {
    private long dim;

    public boolean allEmpty;
    public boolean foundEmpty;
    public ArrayList<Long> indices;
    public Dimension(long d) {
        dim = d;
        allEmpty = true;
        foundEmpty = false;
        indices = new ArrayList<>();
    }

    public void increment() {
        dim++;
    }

    public long getDim() {
        return dim;
    }

    public String toString() {
        String s = "";
        ArrayList<Long> rev = new ArrayList<>(indices);
        Collections.reverse(rev);
        for (int i = 0; i< rev.size();i++) {
            if (rev.get(i) != null){
                s += "["+ rev.get(i)+"]";
            }else{
                s += "[]";
            }
        }
        return s;
    }
}
