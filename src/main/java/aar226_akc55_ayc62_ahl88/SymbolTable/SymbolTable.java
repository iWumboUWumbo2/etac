package aar226_akc55_ayc62_ahl88.SymbolTable;

import aar226_akc55_ayc62_ahl88.newast.expr.Id;

import java.lang.reflect.Array;
import java.util.*;

public class SymbolTable<T> {

    private ArrayList<HashMap<String, T>> scopes;

    public SymbolTable() {
        this.scopes = new ArrayList<>();
    }

    /**
     * Looks up an identifier within our scope
     *
     * @param id The identifier
     * @return The type of the identifier, if it exists.
     * @throws Error if the identifier is not found
     */
    public T lookup(Id id) throws Error {
        ArrayList<HashMap<String, T>> rev = new ArrayList<>(scopes);
        Collections.reverse(rev);
        for (HashMap<String, T> scope: rev){
            if (scope.containsKey(id.toString())){
                return scope.get(id.toString());
            }
        }
        throw new Error(id.getLine() +":" +  id.getColumn() + " Semantic error: Identifier not found");
    }

    /**
     * Does the symboltable contain the identifer
     * @param id The identifier.
     * @return true if the identifier exist. False Otherwise
     */
    public boolean contains(Id id){
        ArrayList<HashMap<String, T>> rev = new ArrayList<>(scopes);
        Collections.reverse(rev);
        for (HashMap<String, T> scope: rev){
            if (scope.containsKey(id.toString())){
                return true;
            }
        }
        return false;
    }

    /**
     * Add an identifier to the current scope with a given type.
     * @param id The identifier.
     * @param type The type.
     */
    public void add(Id id, T type){
        if (scopes.size() == 0){
            throw new Error(id.getLine() + ":" + id.getColumn() +" Semantic error: There is no Scope Somehow");
        }
        HashMap<String,T> last = scopes.get(scopes.size() - 1);
        last.put(id.toString(),type);
    }

    /**
     * we enter a new scope
     */
    void enterScope(){
        scopes.add(new HashMap<>());
    }

    /**
     * We exit the current scope.
     */
    void exitScope(){
        scopes.remove(scopes.size() - 1);
    }

}