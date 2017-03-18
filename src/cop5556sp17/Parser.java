package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;
	//Token ft;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = term();
		while(t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)){
		Token op = t;
		switch(t.kind){
		case LT: 
		case LE:
		case GT:
		case GE:
		case EQUAL:
		case NOTEQUAL:
			consume();
			break;
		default: throw new SyntaxException("Illegal character encountered!");
		}
		e1 = term();
		e0 = new BinaryExpression(ft,e0,op,e1);
		}
		return e0;
	}

	Expression term() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = elem();
		while(t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)){
			Token op = t;
			switch(t.kind){
			case PLUS: consume(); break;
			case MINUS: consume(); break;
			case OR: consume(); break;
			}
			e1 = elem();
			e0 = new BinaryExpression(ft,e0,op,e1);
		}
		return e0;
	}

	Expression elem() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = factor();
		while(t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)){
			Token op = t;
			switch(t.kind){
			case TIMES: consume();
			break;
			case DIV:
				consume();
				break;
			case AND:
				consume();
				break;
			case MOD:
				consume();
				break;
			default: throw new SyntaxException("Illegal character in elem");
			}
			e1 = factor();
			e0 = new BinaryExpression(ft,e0,op,e1);
		}
		return e0;
	}

	Expression factor() throws SyntaxException {
		Token ft = t;
		Expression e0 = null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			//System.out.println(t);
			consume();
			e0 = new IdentExpression(ft);
			//System.out.println(t);
		}
			break;
		case INT_LIT: {
			consume();
			e0 = new IntLitExpression(ft);
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
			e0 = new BooleanLitExpression(ft);
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
			e0 = new ConstantExpression(ft);
		}
			break;
		case LPAREN: {
			consume();
			e0 = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		return e0;
	}

	Block block() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Block b = null;
		ArrayList<Dec> d = new ArrayList<Dec>();
		ArrayList<Statement> s = new ArrayList<Statement>();
		//System.out.println(t.getText());
		match(LBRACE);
		//System.out.println(t.getText());
		if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)){
			//d = new ArrayList<Dec>();
			while(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)){
				d.add(dec());
			}
		}
		if(t.isKind(OP_SLEEP)||t.isKind(KW_WHILE)||t.isKind(KW_IF)||t.isKind(IDENT)/*||t.isKind(KW_IDENT)*/){
			//s = new ArrayList<Statement>();
			//System.out.println(t.getText());
			while(t.isKind(OP_SLEEP)||t.isKind(KW_WHILE)||t.isKind(KW_IF)||t.isKind(IDENT)/*||t.isKind(KW_IDENT)*/){
				s.add(statement());
			}
		}
		match(RBRACE);
		b = new Block(ft,d,s);
		return b;
	}

	Program program() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Program p = null;
		Block b = null;
		ArrayList<ParamDec> pd = new ArrayList<ParamDec>();
		match(IDENT);
		if(t.isKind(LBRACE)){
			b = block();
		}
		else if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
			//pd = new ArrayList<ParamDec>();
			pd.add(paramDec());
			while(t.isKind(COMMA)){
				match(COMMA);
				pd.add(paramDec());
			}
			b = block();
		}
		else throw new SyntaxException("Expected block or parameter declaration");
		p = new Program(ft,pd,b);
		return p;
	}

	ParamDec paramDec() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		switch(t.kind){
		case KW_URL: match(KW_URL);
		break;
		case KW_FILE: match(KW_FILE);
		break;
		case KW_INTEGER: match(KW_INTEGER);
		break;
		case KW_BOOLEAN: match(KW_BOOLEAN);
		break;
		default: throw new SyntaxException("Expected parameter declaration");
		}
		Token id = t;
		match(IDENT);
		return new ParamDec(ft,id);
	}

	Dec dec() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		switch(t.kind){
		case KW_IMAGE: match(KW_IMAGE);
		break;
		case KW_FRAME: match(KW_FRAME);
		break;
		case KW_INTEGER: match(KW_INTEGER);
		break;
		case KW_BOOLEAN: match(KW_BOOLEAN);
		break;
		default: throw new SyntaxException("Expected dec");
		}
		Token id = t;
		match(IDENT);
		return new Dec(ft,id);
	}

	Statement statement() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Statement st = null;
		if(t.isKind(OP_SLEEP)){
			match(OP_SLEEP);
			st = new SleepStatement(ft, expression());
			match(SEMI);
		}
		else if(t.isKind(KW_WHILE)){
			st = whileStatement();
		}
		else if(t.isKind(KW_IF)){
			st = ifStatement();
		}
		else if(t.isKind(IDENT)){
			Token t1 = scanner.peek();
			if(t1.isKind(ASSIGN)){
				st = assign();
				match(SEMI);
			}
			else if(t1.isKind(ARROW) || t1.isKind(BARARROW)){
				st = chain();
				match(SEMI);
			}
			else throw new SyntaxException("Illegal factor in statement");
		}
		else throw new SyntaxException("Illegal factor in statement");
		return st;
	}
	
	Statement whileStatement() throws SyntaxException {
		Token ft = t;
		Expression e = null;
		Block b = null;
		match(KW_WHILE);
		match(LPAREN);
		e = expression();
		match(RPAREN);
		b = block();
		return new WhileStatement(ft,e,b);
	}
	
	Statement ifStatement() throws SyntaxException {
		Token ft = t;
		Expression e = null;
		Block b = null;
		match(KW_IF);
		match(LPAREN);
		e = expression();
		match(RPAREN);
		b = block();
		return new IfStatement(ft,e,b);
	}
	
	Statement assign() throws SyntaxException {
		Token ft = t;
		IdentLValue i = new IdentLValue(t);
		Expression e = null;
		match(IDENT);
		match(ASSIGN);
		e = expression();
		return new AssignmentStatement(ft,i,e);
	}

	Chain chain() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		Chain c0 = null;
		ChainElem c1 = null;
		c0 = chainElem();
		Token op = t;
		switch(t.kind){
		case ARROW: match(ARROW);
		break;
		case BARARROW: match(BARARROW);
		break;
		default: throw new SyntaxException("Illegal character in chain");
		}
		c1 = chainElem();
		c0 = new BinaryChain(ft,c0,op,c1);
		while(t.isKind(ARROW) || t.isKind(BARARROW)){
			op = t;
			switch(t.kind){
			case ARROW: match(ARROW);
			break;
			case BARARROW: match(BARARROW);
			break;
			}
			c1 = chainElem();
			c0 = new BinaryChain(ft,c0,op,c1);
		}
		return c0;
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		ChainElem ce = null;
		Tuple tuple = null;
		if(t.isKind(IDENT)){
			ce = new IdentChain(ft);
			match(IDENT);
		}
		else if(t.isKind(OP_BLUR)||t.isKind(OP_GRAY)||t.isKind(OP_CONVOLVE)){
			consume();
			tuple = arg();
			ce = new FilterOpChain(ft,tuple);
		}
		else if(t.isKind(KW_SHOW)|| t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC)){
			consume();
			tuple = arg();
			ce = new FrameOpChain(ft,tuple);
		}
		else if(t.isKind(OP_WIDTH)|| t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)){
			consume();
			tuple = arg();
			ce = new ImageOpChain(ft,tuple);
		}
		else throw new SyntaxException("Illegal character in chainElem");
		return ce;
	}

	Tuple arg() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		Token ft = t;
		ArrayList<Expression> args = new ArrayList<Expression>();
		if(t.isKind(LPAREN)){
			//args = new ArrayList<Expression>();
			match(LPAREN);
			args.add(expression());
			while(t.isKind(COMMA)){
				match(COMMA);
				args.add(expression());
			}
			match(RPAREN);
			//return new Tuple(ft, args); 
		}
		return new Tuple(ft, args); 
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		if(t.isKind(IDENT)) throw new SyntaxException("saw " + t.getText() + " expected " + kind);
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
