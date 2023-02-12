package aar226_akc55_ayc62_ahl88.ast;

import java.util.ArrayList;

public class Method implements Definition{
    private Id id;
    private ArrayList<Decl> decls;
    private ArrayList<Type> types;
    private Block block;

//    , Block b
//    public Method(String s, ArrayList<Decl> d, ArrayList<Type> t, Block b){
//        id = new Id(s);
//        decls = d;
//        types = t;
//        block = b;
//    }

    public Method(String s, ArrayList<Decl> d, ArrayList<Type> t){
        id = new Id(s);
        decls = d;
        types = t;
    }
    public String toString(){
        String build = "";
        build +=  "( " + id.toString() + " " + decls.toString() + " " + types.toString() +  " )";
        return build;
    }
}
