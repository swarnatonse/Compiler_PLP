package cop5556sp17.AST;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public class IdentLValue extends ASTNode {
	
	Dec d;
	
	public IdentLValue(Token firstToken) {
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
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}

}
