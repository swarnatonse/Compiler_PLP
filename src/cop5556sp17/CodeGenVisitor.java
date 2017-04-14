package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.currSlot = 0;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	int currSlot;
	static int currParamSlot = 0;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		
		for( Dec d: program.getB().getDecs() ){
			mv.visitLocalVariable(d.getIdent().getText(), classDesc, null, startRun, endRun, d.slotNumber);
		}
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		MethodVisitor mv = (MethodVisitor) arg;
		
		Chain ch0 = binaryChain.getE0();
		ChainElem ch1 = binaryChain.getE1();
		
		Token arrow = binaryChain.getArrow();
		
		mv.visitInsn(ICONST_0);
		ch0.visit(this, mv);
		
		mv.visitInsn(ICONST_1);
		ch1.visit(this, mv);
		
		// Add code for processing arrow ??
		
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		
		MethodVisitor mv = (MethodVisitor) arg;
		 Expression e0 = binaryExpression.getE0();
		 Expression e1 = binaryExpression.getE1();
		 
		 e0.visit(this, mv);
		 e1.visit(this, mv);
		 
		 Token op = binaryExpression.getOp();
		 
		 switch(op.kind){
		 case PLUS: mv.visitInsn(IADD); break;
		 case MINUS: mv.visitInsn(ISUB); break;
		 case TIMES: mv.visitInsn(IMUL); break;
		 case DIV: mv.visitInsn(IDIV); break;
		 case AND: mv.visitInsn(IAND); break;
		 case OR: mv.visitInsn(IOR); break;
		 case LT:{
				Label label1 = new Label();
				Label label2 = new Label();
				mv.visitJumpInsn(IF_ICMPLT, label1);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(label2);
				break;
			}
		 case GT:{
				Label label1 = new Label();
				Label label2 = new Label();
				mv.visitJumpInsn(IF_ICMPGT, label1);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(label2);
				break;
			}
		 case GE:{
				Label label1 = new Label();
				Label label2 = new Label();
				mv.visitJumpInsn(IF_ICMPGE, label1);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(label2);
				break;
			}
		 case LE:{
				Label label1 = new Label();
				Label label2 = new Label();
				mv.visitJumpInsn(IF_ICMPLE, label1);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(label2);
				break;
			}
		 case EQUAL:{
				Label label1 = new Label();
				Label label2 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, label1);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(label2);
				break;
			}
		 case NOTEQUAL:{
				Label label1 = new Label();
				Label label2 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, label1);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(label2);
				break;
			}
		 
		 }
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		for(Dec d: block.getDecs()){
			d.visit(this, mv);
		}
		for(Statement s: block.getStatements()){
			s.visit(this, mv);
		}
		
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		Boolean boolval = booleanLitExpression.getValue();
		mv.visitLdcInsn(boolval);
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		declaration.slotNumber = currSlot; // Swarna - here
		mv.visitInsn(ICONST_0);
		currSlot++;
		mv.visitVarInsn(ISTORE, currSlot);
		
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		MethodVisitor mv = (MethodVisitor) arg;
		mv.visitInsn(POP); 
		Kind kind = filterOpChain.getFirstToken().kind;
		switch(kind){
		case OP_BLUR: mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "blurOp",PLPRuntimeFilterOps.opSig, false);
		break;
		case OP_GRAY: mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "grayOp",PLPRuntimeFilterOps.opSig, false);
		break;
		case OP_CONVOLVE: mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "convolveOp",PLPRuntimeFilterOps.opSig, false);
		break;
		default:
			break;
		}
		
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		MethodVisitor mv = (MethodVisitor) arg;
		frameOpChain.getArg().visit(this, mv);
		mv.visitInsn(POP); 
		Kind kind = frameOpChain.getFirstToken().kind;
		switch(kind){
		case KW_SHOW: mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "showImage",PLPRuntimeFrame.showImageDesc, false);
		break;
		case KW_HIDE: mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "hideImage",PLPRuntimeFrame.hideImageDesc, false);
		break;
		case KW_XLOC: mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "getXVal",PLPRuntimeFrame.getXValDesc, false);
		break;
		case KW_YLOC: mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "getYVal",PLPRuntimeFrame.getYValDesc, false);
		break;
		case KW_MOVE: mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "moveFrame",PLPRuntimeFrame.moveFrameDesc, false);
		break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		Label left = new Label();
		Label end = new Label();
		
		Dec d = identChain.getDec();
		TypeName type = identChain.getTypeName();
		String iden = identChain.getFirstToken().getText();
		String jvmType = type.getJVMTypeDesc();
		
		mv.visitInsn(ICONST_0);
		mv.visitJumpInsn(IF_ICMPEQ, left);
		
		// add code for right
		if(identChain.getTypeName() == TypeName.INTEGER || identChain.getTypeName() == IMAGE){
			
			if( d instanceof ParamDec){
				mv.visitVarInsn(ALOAD, 0);
				mv.visitInsn(SWAP);
				mv.visitFieldInsn(PUTFIELD, className, iden, jvmType);
			}
			else{
				mv.visitVarInsn(ISTORE, d.slotNumber);
			}
		}
		else if(identChain.getTypeName() == TypeName.FILE){
			// TODO - 
		}
		else if(identChain.getTypeName() == TypeName.FRAME){
			// TODO
		}
		mv.visitJumpInsn(GOTO, end);
		
		mv.visitLabel(left);
		
		if(d instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, iden, jvmType);
		}
		else{
			mv.visitVarInsn(ILOAD, d.slotNumber);
		}
		
		mv.visitLabel(end);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		Dec d = identExpression.getDec();
		String iden = identExpression.getFirstToken().getText();
		String jvmType = d.getTypeName().getJVMTypeDesc();
		
		if( d instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, iden, jvmType);
		}
		else{
			mv.visitVarInsn(ILOAD, d.slotNumber);
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		Dec d = identX.getDec();
		String iden = identX.getFirstToken().getText();
		String jvmType = d.getTypeName().getJVMTypeDesc();
		
		if( d instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, iden, jvmType);
		}
		else{
			mv.visitVarInsn(ISTORE, d.slotNumber);
		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv =(MethodVisitor) arg;
		
		Expression e = ifStatement.getE();
		Block b  = ifStatement.getB();
		
		Label AFTER = new Label();
		
		mv.visitInsn(ICONST_0);
		mv.visitJumpInsn(IF_ICMPEQ, AFTER);
		b.visit(this, mv);
		mv.visitLabel(AFTER);
		
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		MethodVisitor mv = (MethodVisitor) arg;
		imageOpChain.getArg().visit(this, mv);
		mv.visitInsn(POP); 
		Kind kind = imageOpChain.getFirstToken().kind;
		switch(kind){
		case OP_WIDTH: mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth",PLPRuntimeImageOps.getWidthSig, false);
		break;
		case OP_HEIGHT: mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight",PLPRuntimeImageOps.getHeightSig, false);
		break;
		case KW_SCALE: mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "scale",PLPRuntimeImageOps.scaleSig, false);
		break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		int intval = intLitExpression.value;
		mv.visitLdcInsn(intval);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		MethodVisitor mv = (MethodVisitor) arg;
		String ident = paramDec.getIdent().getText();
		String jvmType = paramDec.getTypeName().getJVMTypeDesc();
		
		FieldVisitor fv = cw.visitField(ACC_PUBLIC, ident, jvmType, null, null);
		fv.visitEnd();
		
		mv.visitVarInsn(ALOAD, 0);
		
		mv.visitVarInsn(ALOAD, 1); // Swarna - Here
		mv.visitLdcInsn(currParamSlot);
		mv.visitInsn(AALOAD);
		
		switch(jvmType){
		case "I": mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				break;
		case "Z": mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				break;
		}
		currParamSlot++;
		mv.visitFieldInsn(PUTFIELD, className, ident, jvmType);
		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		//assert false : "not yet implemented";
		MethodVisitor mv = (MethodVisitor) arg;
		for(Expression e: tuple.getExprList()){
			e.visit(this, mv);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		MethodVisitor mv = (MethodVisitor) arg;
		
		Block b = whileStatement.getB();
		Expression e = whileStatement.getE();
		
		Label GUARD = new Label();
		Label BODY = new Label();
		mv.visitJumpInsn(GOTO, GUARD);
		
		mv.visitLabel(BODY);
		b.visit(this, arg);
		
		mv.visitLabel(GUARD);
		e.visit(this, arg);
		
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(IF_ICMPEQ, BODY);
		
		return null;
	}

}
