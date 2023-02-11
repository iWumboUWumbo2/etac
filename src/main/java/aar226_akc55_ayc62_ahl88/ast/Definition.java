package aar226_akc55_ayc62_ahl88.ast;

public class Definition {
    private Method method;
    private Globdecl globdecl;

    public Definition( Method m){
        method = m;
    }

    public String toString(){
        return "( " + method.toString() + " )";
    }
}
