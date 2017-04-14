package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("Inside here");
		binaryChain.getE0().visit(this, arg);
		binaryChain.getE1().visit(this, arg);
		Token op = binaryChain.getArrow();
		Chain chain0 = binaryChain.getE0();
		ChainElem chain1 = binaryChain.getE1();
		if(chain0.getTypeName() == URL && chain1.getTypeName() == IMAGE && op.isKind(ARROW)){
			//System.out.println("c1!");
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain0.getTypeName() == FILE && chain1.getTypeName() == IMAGE && op.isKind(ARROW)){
			//System.out.println("c2!");
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain0.getTypeName() == FRAME && chain1 instanceof FrameOpChain && (chain1.getFirstToken().isKind(KW_SHOW)||chain1.getFirstToken().isKind(KW_HIDE)||chain1.getFirstToken().isKind(KW_MOVE)) && op.isKind(ARROW)){
			//System.out.println("c3!");
			binaryChain.setTypeName(FRAME);
		}
		else if(chain0.getTypeName() == FRAME && chain1 instanceof FrameOpChain && (chain1.getFirstToken().isKind(KW_XLOC)||chain1.getFirstToken().isKind(KW_YLOC)) && op.isKind(ARROW)){
			//System.out.println("c4!");
			binaryChain.setTypeName(INTEGER);
		}
		else if(chain0.getTypeName() == IMAGE && chain1 instanceof ImageOpChain && (chain1.getFirstToken().isKind(OP_WIDTH)||chain1.getFirstToken().isKind(OP_HEIGHT))&& op.isKind(ARROW)){
			//System.out.println("c5!");
			binaryChain.setTypeName(INTEGER);
		}
		else if(chain0.getTypeName() == IMAGE && chain1.getTypeName() == FRAME && op.isKind(ARROW)){
			//System.out.println("c6!");
			binaryChain.setTypeName(FRAME);
		}
		else if(chain0.getTypeName() == IMAGE && chain1.getTypeName() == FILE && op.isKind(ARROW)){
			//System.out.println("c7!");
			binaryChain.setTypeName(NONE);
		}
		else if(chain0.getTypeName() == IMAGE && chain1 instanceof FilterOpChain && (chain1.getFirstToken().isKind(OP_GRAY)||chain1.getFirstToken().isKind(OP_BLUR)||chain1.getFirstToken().isKind(OP_CONVOLVE))){
			//System.out.println("c8!");
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain0.getTypeName() == IMAGE && chain1 instanceof ImageOpChain && chain1.getFirstToken().isKind(KW_SCALE) && op.isKind(ARROW)){
			//System.out.println("c9!");
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain0.getTypeName() == IMAGE && chain1 instanceof IdentChain && op.isKind(ARROW)){
			//System.out.println("c!");
			binaryChain.setTypeName(IMAGE);
		}
		else{
			throw new TypeCheckException("BinaryChain type check exception!");
		}
		return binaryChain.getTypeName();
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);
		TypeName e0type = binaryExpression.getE0().getTypeName();
		TypeName e1type = binaryExpression.getE1().getTypeName();
		Token op = binaryExpression.getOp();
		
		if(e0type == INTEGER && e1type == INTEGER && (op.isKind(PLUS)||op.isKind(MINUS))){
			binaryExpression.setTypeName(INTEGER);
		}
		else if(e0type == IMAGE && e1type == IMAGE && (op.isKind(PLUS)||op.isKind(MINUS))){
			binaryExpression.setTypeName(IMAGE);
		}
		else if(e0type == INTEGER && e1type == INTEGER && (op.isKind(TIMES)||op.isKind(DIV))){
			binaryExpression.setTypeName(INTEGER);
		}
		else if(e0type == INTEGER && e1type == IMAGE && (op.isKind(TIMES))){
			binaryExpression.setTypeName(IMAGE);
		}
		else if(e0type == IMAGE && e1type == INTEGER && (op.isKind(TIMES))){
			binaryExpression.setTypeName(IMAGE);
		}
		else if(e0type == INTEGER && e1type == INTEGER && (op.isKind(LT)||op.isKind(LE)||op.isKind(GE)||op.isKind(GT))){
			binaryExpression.setTypeName(BOOLEAN);
		}
		else if(e0type == BOOLEAN && e1type == BOOLEAN && (op.isKind(LT)||op.isKind(LE)||op.isKind(GE)||op.isKind(GT))){
			binaryExpression.setTypeName(BOOLEAN);
		}
		else if(op.isKind(EQUAL)||op.isKind(NOTEQUAL)){
			if(e0type != e1type){
				throw new TypeCheckException("Types of expression not equal for EQUAL/NOTEQUAL");
			}
			else
				binaryExpression.setTypeName(BOOLEAN);
		}
		else{
			throw new TypeCheckException("Binary Expression type check exception!");
		}
		
		return binaryExpression.getTypeName();
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		for(Dec d: block.getDecs()){
			d.visit(this, arg);
		}
		for(Statement s: block.getStatements()){
			s.visit(this, arg);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.setTypeName(BOOLEAN);
		return BOOLEAN;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		filterOpChain.getArg().visit(this, arg);
		if(filterOpChain.getArg().getExprList().size() != 0){
			throw new TypeCheckException("Tuple length is not 0 for filteropchain");
		}
		filterOpChain.setTypeName(IMAGE);
		return filterOpChain.getTypeName();
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		frameOpChain.getArg().visit(this, arg);
		Token frameop = frameOpChain.getFirstToken();
		if(frameop.isKind(KW_SHOW)||frameop.isKind(KW_HIDE)){
			if(frameOpChain.getArg().getExprList().size() != 0){
				throw new TypeCheckException("Tuple length is not 0 for frameopchain");
			}
			frameOpChain.setTypeName(NONE);
		}
		else if(frameop.isKind(KW_XLOC)||frameop.isKind(KW_YLOC)){
			if(frameOpChain.getArg().getExprList().size() != 0){
				throw new TypeCheckException("Tuple length is not 0 for frameopchain");
			}
			frameOpChain.setTypeName(INTEGER);
		}
		else if(frameop.isKind(KW_MOVE)){
			if(frameOpChain.getArg().getExprList().size() != 2){
				throw new TypeCheckException("Tuple length is not 2 for frameopchain");
			}
			frameOpChain.setTypeName(NONE);
		}
		else{
			throw new Exception("Parser error!");
		}
		return frameOpChain.getTypeName();
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec result = symtab.lookup(identChain.getFirstToken().getText());
		if(result == null){
			throw new TypeCheckException("Ident is not visible in current scope!");
		}
		identChain.setDec(result);
		identChain.setTypeName(result.getTypeName());
		return identChain.getTypeName();
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec result = symtab.lookup(identExpression.getFirstToken().getText());
		if(result == null){
			throw new TypeCheckException("Ident is not visible in current scope!");
		}
		identExpression.setDec(result);
		identExpression.setTypeName(result.getTypeName());
		return identExpression.getTypeName();
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		ifStatement.getE().visit(this, arg);
		ifStatement.getB().visit(this, arg);
		if(ifStatement.getE().getTypeName() != BOOLEAN){
			throw new TypeCheckException("If Statement expression not boolean!");
		}
		return BOOLEAN;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setTypeName(INTEGER);
		return INTEGER;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		sleepStatement.getE().visit(this, arg);
		if(sleepStatement.getE().getTypeName() != INTEGER){
			throw new TypeCheckException("Sleep Statement Expression is not INTEGER!");
		}
		return INTEGER;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		whileStatement.getE().visit(this, arg);
		whileStatement.getB().visit(this, arg);
		if(whileStatement.getE().getTypeName() != BOOLEAN){
			throw new TypeCheckException("While Statement expression not boolean!");
		}
		return BOOLEAN;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName thetype = Type.getTypeName(declaration.getFirstToken());
		declaration.setTypeName(thetype);
		if(symtab.insert(declaration.getIdent().getText(), declaration) == false){
			throw new Exception("Redeclaration error");
		}
		return thetype;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(ParamDec p: program.getParams()){
			p.visit(this, arg);
		}
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		assignStatement.getVar().visit(this, arg);
		assignStatement.getE().visit(this, arg);
		if(assignStatement.getVar().getTypeName() != assignStatement.getE().getTypeName()){
			throw new TypeCheckException("Assignment Statement IdentLValue not equal to Expression Type!");
		}
		return assignStatement.getVar().getTypeName();
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec result = symtab.lookup(identX.getFirstToken().getText());
		if(result == null){
			throw new TypeCheckException("Ident is not visible in current scope!");
		}
		identX.setDec(result);
		return identX.getTypeName();
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName thetype = Type.getTypeName(paramDec.getFirstToken());
		paramDec.setTypeName(thetype);
		if(symtab.insert(paramDec.getIdent().getText(), paramDec) == false){
			throw new TypeCheckException("Redeclaration occurred!");
		}
		return thetype;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setTypeName(INTEGER);
		return INTEGER;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		imageOpChain.getArg().visit(this, arg);
		Token imageop = imageOpChain.getFirstToken();
		if(imageop.isKind(OP_WIDTH)||imageop.isKind(OP_HEIGHT)){
			if(imageOpChain.getArg().getExprList().size() != 0){
				throw new TypeCheckException("Tuple length is not 0 for imageopchain");
			}
			imageOpChain.setTypeName(INTEGER);
		}
		else if(imageop.isKind(KW_SCALE)){
			if(imageOpChain.getArg().getExprList().size() != 1){
				throw new TypeCheckException("Tuple length is not 1 for imageopchain");
			}
			imageOpChain.setTypeName(IMAGE);
		}
		
		return imageOpChain.getTypeName();
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(Expression e: tuple.getExprList()){
			e.visit(this, arg);
			if(e.getTypeName() != INTEGER){
				throw new TypeCheckException("Tuple Expression not an Integer!");
			}
		}
		return null;
	}


}
