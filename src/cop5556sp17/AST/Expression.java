package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	
	TypeName tn;
	
	protected Expression(Token firstToken/*, TypeName typename*/) {
		super(firstToken);
		//this.tn = typename;
	}

	public void setTypeName(TypeName tn){
		this.tn = tn;
	}
	
	public TypeName getTypeName(){
		return tn;
	}
	
	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
