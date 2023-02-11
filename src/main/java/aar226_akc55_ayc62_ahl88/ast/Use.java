package aar226_akc55_ayc62_ahl88.ast;

public class Use {
//    private Id id;
//
//    public Use(Id id) {
//        this.id = id;
//    }
    private String s;
    public Use(String id) {
        s = id;
    }
    public String toString(){
        return "(" + "use " + s + ")";
    }
}
