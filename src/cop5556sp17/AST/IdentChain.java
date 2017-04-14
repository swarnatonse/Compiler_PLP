package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public class IdentChain extends ChainElem {
	
	Dec d;

	public IdentChain(Token firstToken) {
		super(firstToken);
	}
	
	public void setDec(Dec d){
		this.d = d;
	}
	
	public Dec getDec(){
		return d;
	}

	@Override
	public String toString() {
		return "IdentChain [firstToken=" + firstToken + "]";
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentChain(this, arg);
	}

}
