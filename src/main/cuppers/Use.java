package aar226_akc55_ayc62_ahl88.ast;

public class Use {
    private Id id;

    public Use(Id id) {
        this.id = id;
    }
    public Use(String s) {
        this.id = new Id(s);
    }
    public String toString(){
        return "(" + "use " + id.toString() + ")";
    }
}
