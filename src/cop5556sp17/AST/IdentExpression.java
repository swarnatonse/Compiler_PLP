package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.Token;

public class IdentExpression extends Expression {
	
	Dec d;

	public IdentExpression(Token firstToken) {
		super(firstToken);
	}
	
	public void setDec(Dec d) throws SyntaxException{
		this.d = d;
		//d.setTypeName(Type.getTypeName(d.getFirstToken()));
	}
	
	public Dec getDec(){
		return d;
	}
	
	public TypeName getTypeName(){
		return d.getTypeName();
	}

	@Override
	public String toString() {
		return "IdentExpression [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentExpression(this, arg);
	}

}
