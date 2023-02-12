package aar226_akc55_ayc62_ahl88.ast;

public class Decl {
    private Id id;
    private Type type;


    public Decl( String s, Type t){
        id = new Id(s);
        type = t;
    }

    public String toString(){
        return "(" + id.toString() + ":" + type.toString() + ")";
    }
}
