package cop5556sp17;

import java.util.ArrayList;
import java.util.Arrays;

public class Scanner {
	
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			
			return chars.substring(pos, pos+length);
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//System.out.println(Arrays.toString(lines.toArray()));
			int linenumber = returnlineno(pos);
			//System.out.println(linenumber);
			LinePos lp = new LinePos(linenumber, pos-lines.get(linenumber));
			return lp;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}
		
		int returnlineno(int pos){ //Change to binary search later
			int i;
			for(i = 0; i<lines.size()-1; i++){
				if(lines.get(i) <= pos && lines.get(i+1) > pos){
					return i;
				}
			}
			return i;
		}
		
		boolean isKind(Kind k){
			if(this.kind == k) return true;
			return false;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			if(kind == Kind.INT_LIT){
				String num = chars.substring(pos, pos+length);
				return Integer.parseInt(num);
			}
			return 0;
		}
		@Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		 

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
		
	}

	 


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		lines = new ArrayList<Integer>();

	}
	public enum State{
		START, IN_IDENT, IN_DIGIT, AFTER_FS, AFTER_OR, AFTER_OR_DASH, AFTER_EQ, AFTER_DASH, AFTER_EX, 
		AFTER_LT, AFTER_GT, IN_COMMENT, IN_COMMENT_STAR, AFTER_BS, AFTER_CR;
	}

	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		lines.add(0);
		//TODO IMPLEMENT THIS!!!!
		
		int length = chars.length();
		State state = State.START;
		int startPos = 0;
		int ch;
		State prev_state = State.START;
		while(pos <= length){
			ch = pos < length ? chars.charAt(pos) : -1;
			switch(state){
			case START: {
				pos = skipWhiteSpaces(pos);
				ch = pos < length ? chars.charAt(pos):-1;
				startPos = pos;
				switch(ch){
				case '/': { state = State.AFTER_FS; pos++;} break;
				case ';': { tokens.add(new Token(Kind.SEMI, pos, 1)); pos++;} break;
				case ',': { tokens.add(new Token(Kind.COMMA, pos, 1)); pos++;} break;
				case '(': { tokens.add(new Token(Kind.LPAREN, pos, 1)); pos++;} break;
				case ')': { tokens.add(new Token(Kind.RPAREN, pos, 1)); pos++;} break;
				case '{': { tokens.add(new Token(Kind.LBRACE, pos, 1)); pos++;} break;
				case '}': { tokens.add(new Token(Kind.RBRACE, pos, 1)); pos++;} break;
				case '&': { tokens.add(new Token(Kind.AND, pos, 1)); pos++;} break;
				case '+': { tokens.add(new Token(Kind.PLUS, pos, 1)); pos++;} break;
				case '*': { tokens.add(new Token(Kind.TIMES, pos, 1)); pos++;} break;
				case '%': { tokens.add(new Token(Kind.MOD, pos, 1)); pos++;} break;
				case '0': { tokens.add(new Token(Kind.INT_LIT, pos, 1)); pos++;} break;
				case '|': { state = State.AFTER_OR; pos++;} break;
				case '<': { state = State.AFTER_LT; pos++;} break;
				case '>': { state = State.AFTER_GT; pos++;} break;
				case '-': { state = State.AFTER_DASH; pos++;} break;
				case '!': { state = State.AFTER_EX; pos++;} break;
				case '=': { state = State.AFTER_EQ; pos++;} break;
				case '\n': 
				case '\r':{ state = State.AFTER_BS; prev_state = State.START;} break;
				case -1: { tokens.add(new Token(Kind.EOF,pos,0)); pos++;} break;
				default: {
					if (Character.isDigit(ch)) {state = State.IN_DIGIT;pos++;} 
		            else if (Character.isJavaIdentifierStart(ch)) {
		                 state = State.IN_IDENT;pos++;
		             } 
		             else {throw new IllegalCharException(
		                        "illegal char " +ch+" at pos "+pos);
		             }

				}
				}
			}
			break;
			case AFTER_FS: {
				if(ch == '*'){
					state = State.IN_COMMENT;
					pos++;
				}
				else {
					tokens.add(new Token(Kind.DIV, startPos, 1));
					state = State.START;
				}
			}
			break;
			case IN_COMMENT: {
				if( ch == '\n' ){
					state = State.AFTER_BS;
					prev_state = State.IN_COMMENT;
				}
				if(ch == '*'){
					state = State.IN_COMMENT_STAR;
				}
				if(pos >= chars.length()) state = State.START;
				pos++;
			}
			break;
			case IN_COMMENT_STAR: {
				if(pos >= chars.length()){
					state = State.START;
				}
				else{
					if(ch == '/'){
						state = State.START;
						pos++;
					}
					else{
						state = State.IN_COMMENT;
					}
				}
			}
			break;
			case IN_IDENT: {
				if (Character.isJavaIdentifierPart(ch)) {
		            pos++;
				} else {
					String sub = chars.substring(startPos, pos);
					//if(sub.equals("integer") || sub.equals("boolean") || sub.equals("image") || sub.equals("url") || sub.equals("file") || sub.equals("frame") || sub.equals("while") || sub.equals("if") || sub.equals("sleep") || sub.equals("screenheight") || sub.equals("screenwidth")){
					if(sub.equals("integer")){	
						tokens.add(new Token(Kind.KW_INTEGER, startPos, pos - startPos));
					}
					else if(sub.equals("boolean")){
						tokens.add(new Token(Kind.KW_BOOLEAN, startPos, pos - startPos));
					}
					else if(sub.equals("image")){
						tokens.add(new Token(Kind.KW_IMAGE, startPos, pos - startPos));
					}
					else if(sub.equals("url")){
						tokens.add(new Token(Kind.KW_URL, startPos, pos - startPos));
					}
					else if(sub.equals("file")){
						tokens.add(new Token(Kind.KW_FILE, startPos, pos - startPos));
					}
					else if(sub.equals("frame")){
						tokens.add(new Token(Kind.KW_FRAME, startPos, pos - startPos));
					}
					else if(sub.equals("while")){
						tokens.add(new Token(Kind.KW_WHILE, startPos, pos - startPos));
					}
					else if(sub.equals("if")){
						tokens.add(new Token(Kind.KW_IF, startPos, pos - startPos));
					}
					else if(sub.equals("screenheight")){
						tokens.add(new Token(Kind.KW_SCREENHEIGHT, startPos, pos - startPos));
					}
					else if(sub.equals("screenwidth")){
						tokens.add(new Token(Kind.KW_SCREENWIDTH, startPos, pos - startPos));
					}
					else if(sub.equals("gray")){
						tokens.add(new Token(Kind.OP_GRAY, startPos, pos-startPos));
					}
					else if(sub.equals("convolve")){
						tokens.add(new Token(Kind.OP_CONVOLVE, startPos, pos-startPos));
					}
					else if(sub.equals("blur")){
						tokens.add(new Token(Kind.OP_BLUR, startPos, pos-startPos));
					}
					else if(sub.equals("scale")){
						tokens.add(new Token(Kind.KW_SCALE, startPos, pos-startPos));
					}
					else if(sub.equals("width")){
						tokens.add(new Token(Kind.OP_WIDTH, startPos, pos-startPos));
					}
					else if(sub.equals("height")){
						tokens.add(new Token(Kind.OP_HEIGHT, startPos, pos-startPos));
					}
					else if(sub.equals("xloc")){
						tokens.add(new Token(Kind.KW_XLOC, startPos, pos-startPos));
					}
					else if(sub.equals("yloc")){
						tokens.add(new Token(Kind.KW_YLOC, startPos, pos-startPos));
					}
					else if(sub.equals("hide")){
						tokens.add(new Token(Kind.KW_HIDE, startPos, pos-startPos));
					}
					else if(sub.equals("show")){
						tokens.add(new Token(Kind.KW_SHOW, startPos, pos-startPos));
					}
					else if(sub.equals("move")){
						tokens.add(new Token(Kind.KW_MOVE, startPos, pos-startPos));
					}
					else if(sub.equals("true")){
						tokens.add(new Token(Kind.KW_TRUE, startPos, pos-startPos));
					}
					else if(sub.equals("false")){
						tokens.add(new Token(Kind.KW_FALSE, startPos, pos-startPos));
					}
					else if(sub.equals("sleep")){
						tokens.add(new Token(Kind.OP_SLEEP, startPos, pos-startPos));
					}
					else{
						tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
					}
		            state = State.START;
		      }

			}
			break;
			case IN_DIGIT: {
				if(Character.isDigit(ch)){
					//System.out.println(ch);
					pos++;
				}
				else{
					//boolean legal = true;
					String sub = chars.substring(startPos, pos);
					try{
						int num = Integer.parseInt(sub);
						//System.out.println("Come here!");
						tokens.add(new Token(Kind.INT_LIT, startPos, pos-startPos));
						state = State.START;
						
					}
					catch(NumberFormatException n){
						//legal = false;
						throw new IllegalNumberException("Int value "+sub+" out of bounds at pos "+pos);
					}
//					finally{
//						if(legal){
//							
//
//						}
//					}
					
				}
			}
			break;
			case AFTER_OR:{
				if(ch == '-'){
					state = State.AFTER_OR_DASH;
					pos++;
				}
				else{
					tokens.add(new Token(Kind.OR, startPos, 1));
					state = State.START;
				}
			}
			break;
			case AFTER_OR_DASH:{
				if(ch == '>'){
					tokens.add(new Token(Kind.BARARROW, startPos, 3));
					state = State.START;
					pos++;
				}
				else{
					tokens.add(new Token(Kind.OR, startPos, 1));
					tokens.add(new Token(Kind.MINUS, startPos+1, 1));
					state = State.START;
				}
			}
			break;
			case AFTER_LT:{
				if(ch == '='){
					tokens.add(new Token(Kind.LE, startPos, 2));
					state = State.START;
					pos++;
				}
				else if(ch == '-'){
					tokens.add(new Token(Kind.ASSIGN, startPos, 2));
					state = State.START;
					pos++;
				}
				else{
					tokens.add(new Token(Kind.LT, startPos, 1));
					state = State.START;
				}
			}
			break;
			case AFTER_GT:{
				if(ch == '='){
					tokens.add(new Token(Kind.GE, startPos,2));
					state = State.START;
					pos++;
				}
				else{
					tokens.add(new Token(Kind.GT, startPos, 1));
					state = State.START;
				}
			}
			break;
			case AFTER_DASH:{
				if(ch == '>'){
					tokens.add(new Token(Kind.ARROW, startPos, 2));
					state = State.START;
					pos++;
				}
				else{
					tokens.add(new Token(Kind.MINUS, startPos, 1));
					state = State.START;
				}
			}
			break;
			case AFTER_EX:{
				if(ch == '='){
					tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
					state = State.START;
					pos++;
				}
				else{
					tokens.add(new Token(Kind.NOT, startPos, 1));
					state = State.START;
				}
			}
			break;
			case AFTER_EQ:{
				if(ch == '='){
					tokens.add(new Token(Kind.EQUAL, startPos, 2));
					state = State.START;
					pos++;
				}
				else{
					state = State.START;
					throw new IllegalCharException(
	                        "illegal char " +ch+" at pos "+pos);
				}
			}
			break;
			case AFTER_BS: {
				if(ch == '\n'){
					pos++;
					lines.add(pos);
					//System.out.println("Adding after newline "+pos);
					//pos++;
					state = prev_state;
				}
				if(ch == '\r'){
					pos++;
					state = State.AFTER_CR;
				}
//				else if(ch == '\t'){
//					pos+=3;
//					state = prev_state;
//				}
			}
			break;
			case AFTER_CR: {
				if(ch == '\n'){
					//System.out.println("After carriage return");
					pos++;
				}
				else{
					//System.out.println("Adding "+pos);
					lines.add(pos);
					state = prev_state;
				}
			}
			}
		}
			
		
		
		return this;  
	}



	final ArrayList<Token> tokens;
	final ArrayList<Integer> lines;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		
		return t.getLinePos();
	}
	
	public int skipWhiteSpaces(int pos){
		if(pos >= chars.length()) return pos;
		if(chars.charAt(pos) == '\n' || chars.charAt(pos) == '\r'){
			return pos;
		}
		while(Character.isWhitespace(chars.charAt(pos))){
			pos++;
			if(pos >= chars.length()) return pos;
			if(chars.charAt(pos) == '\n' || chars.charAt(pos) == '\r'){
				return pos;
			}
		}
		return pos;
	}


}
