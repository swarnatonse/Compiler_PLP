package cop5556sp17;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import cop5556sp17.AST.Dec;


public class SymbolTable {

	int current_scope;
	int next_scope;
	Stack<Integer> scope_stack;
	HashMap<String,HashMap<Integer, Dec>> symtab;

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		current_scope = next_scope++;
		//System.out.println("Entering "+current_scope);
		scope_stack.push(current_scope);
	}
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		scope_stack.pop();
		current_scope = scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		if(symtab.get(ident) == null){
			symtab.put(ident, new HashMap<Integer, Dec>());
		}
		else{
			if(symtab.get(ident).get(current_scope) != null){
				return false;
			}
		}
		symtab.get(ident).put(current_scope, dec);
		return true;
	}
	
	public Dec lookup(String ident){
		if(symtab.get(ident) == null)
			return null;
		else{
			for(int i = scope_stack.size()-1; i>0; i--){
				if(symtab.get(ident).get(scope_stack.get(i)) != null){
					return symtab.get(ident).get(scope_stack.get(i));
				}
			}
			return null;
		}
	}
		
	public SymbolTable() {
		current_scope = 1;
		next_scope = 1;
		scope_stack = new Stack<Integer>();
		symtab = new HashMap<String, HashMap<Integer, Dec>>();
		scope_stack.push(0);
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}
	
	


}
