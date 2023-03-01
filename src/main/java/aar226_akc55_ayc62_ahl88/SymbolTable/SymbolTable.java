package aar226_akc55_ayc62_ahl88.SymbolTable;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;

import java.util.*;

public class SymbolTable<T> {

    private ArrayList<HashMap<String, T>> scopes;
    public Id currentParentFunction;

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
        throw new SemanticError(id.getLine(),id.getColumn() ,"Name " + id.toString() + " cannot be resolved");
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
            throw new SemanticError(id.getLine(),id.getColumn() ,"There is no Scope Somehow");
        }
        HashMap<String,T> last = scopes.get(scopes.size() - 1);
        if (contains(id)){
            throw new SemanticError(id.getLine(),id.getColumn(), "We are adding to context when id already exists");
        }
        last.put(id.toString(),type);
    }

    /**
     * we enter a new scope
     */
    public void enterScope(){
        scopes.add(new HashMap<>());
    }

    /**
     * We exit the current scope.
     */
    public void exitScope(){
        scopes.remove(scopes.size() - 1);
    }

    public Id getCurrentFunction() {
        return currentParentFunction;
    }

    public void printContext(){
        for (HashMap<String, T> s: scopes){
            System.out.println("------------");
            for (HashMap.Entry<String, T> e: s.entrySet()){
                System.out.print (e.getKey());
                System.out.println(" "+ e.getValue().toString());
            }
            System.out.println("------------");
        }
    }
}