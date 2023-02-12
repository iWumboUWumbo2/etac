package aar226_akc55_ayc62_ahl88.ast;

public class Globdecl implements Definition{
    private Decl decl;
    private Value value;

    public Globdecl (Decl d, Value v) {
        decl = d;
        value = v;
    }

    public String toString(){
        String build = "";
        if (value != null){
//            System.out.println("IM HERE");
            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
        }else{
            build +=  "( " + decl.toString() +  " )";
        }
        return build;
    }
}
