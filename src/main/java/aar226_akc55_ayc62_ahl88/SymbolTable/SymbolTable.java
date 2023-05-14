package aar226_akc55_ayc62_ahl88.SymbolTable;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import java.util.*;

public class SymbolTable<T> {
    private ArrayList<HashMap<String, T>> scopes;
    public Id currentParentFunction;
    public boolean parentLoop;
    public HashMap<String, T> allRecordTypes;
    public ArrayList<Id> necessaryDefs;
    public ArrayList<Id> visitedDefs;
    public Id getCurrentFunction() {
        return currentParentFunction;
    }

    public SymbolTable() {
        this.scopes = new ArrayList<>();
        allRecordTypes = new HashMap<String, T>();
        parentLoop = false;
        necessaryDefs = new ArrayList<Id>();
        visitedDefs = new ArrayList<Id>();
    }

    /**
     * Looks up an identifier within our scope
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
     * Checks if the symboltable contains the identifer
     * @param id The identifier.
     * @return True if the identifier exist. False Otherwise
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
     * Checks if the symboltable contains the identifer
     * @param id The identifier.
     * @return True if the identifier exist. False Otherwise
     */
    public boolean contains(String id){
        ArrayList<HashMap<String, T>> rev = new ArrayList<>(scopes);
        Collections.reverse(rev);
        for (HashMap<String, T> scope: rev){
            if (scope.containsKey(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces an id's type for the entire symboltable.
     * @param id Identifier
     */
    public void replace(Id id, T t) {
        for (HashMap<String, T> scope: scopes){
            if (scope.containsKey(id.toString())) {
                scope.replace(id.toString(),t);
            }
        }
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
     * We enter a new scope
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

    /**
     * @return Flattened symboltable
     */
    public HashMap<String, T> flatten() {
        HashMap<String, T> flattened = new HashMap<String, T>();
        for (HashMap<String, T> map: scopes){
            flattened.putAll(map);
        }
        return flattened;
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

    public void addNecessaryDef(Id id) {
        necessaryDefs.add(id);
    }
    public void addVisitedDef(Id id) {
        visitedDefs.add(id);
    }

    public void isNecessaryVisited() {
        ArrayList<String> visitedString = new ArrayList<String>();
        ArrayList<String> necessaryString = new ArrayList<String>();
        for (Id id : visitedDefs) {
            visitedString.add(id.toString());
        }
        for (Id id : necessaryDefs) {
            necessaryString.add(id.toString());
        }
        for (int i = 0; i < necessaryString.size(); i ++ ) {
            if (!visitedString.contains(necessaryString.get(i))) {
                throw new SemanticError(necessaryDefs.get(i).getLine(),
                        necessaryDefs.get(i).getColumn(),
                        "Not all functions in interface declared in module");
            }
        }
    }
}