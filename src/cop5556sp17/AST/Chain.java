package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;


public abstract class Chain extends Statement {

	TypeName tn;
	public boolean isLeft;

	public Chain(Token firstToken/*, TypeName tn*/) {
		super(firstToken);
		//this.tn = tn;
		this.isLeft = false;
	}

	public void setTypeName(TypeName tn){
		this.tn = tn;
	}

	public TypeName getTypeName(){
		return tn;
	}

}
