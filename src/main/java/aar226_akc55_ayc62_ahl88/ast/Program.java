package aar226_akc55_ayc62_ahl88.ast;

import java.util.ArrayList;

public class Program {
    private ArrayList<Use> useList;
    private ArrayList<Definition> definitions;

    public Program(ArrayList<Use> uses, ArrayList<Definition> definitions) {
        useList = uses;
        this.definitions = definitions;
    }

    public String toString() {
        return useList.toString();
    }
}
