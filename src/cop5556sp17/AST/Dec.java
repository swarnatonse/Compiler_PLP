package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class Dec extends ASTNode {
	
	final Token ident;
	TypeName tn;
	public int slotNumber;

	public Dec(Token firstToken, Token ident/*, TypeName tn*/) {
		super(firstToken);

		this.ident = ident;
		//this.tn = tn;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}
	
	public void setTypeName(TypeName tn){
		this.tn = tn;
	}
	
	public TypeName getTypeName(){
		return tn;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}

}
