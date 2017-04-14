/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testSymTab() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y y <- true; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	
	@Test
	public void testSleepStatement() throws Exception{
		String input = "p {\nboolean y sleep 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	
	@Test
	public void testIdentExpression() throws Exception{
		String input = "p {\ninteger y integer z sleep y+z;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	
	@Test
	public void testWhileStatement() throws Exception{
		String input = "p {\nboolean x integer a integer b while(x){integer y sleep y;} if((a+b) != (b*a)){integer y y <- 3;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testBooleanStatement() throws Exception{
		String input = "p {\nboolean x integer y integer z x <- (y == z);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	
	@Test
	public void testRedec() throws Exception{
		String input = "p {\nboolean x integer x }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(Exception.class);
		program.visit(v, null);		
	}
	

	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}		
	
	@Test
	public void testChain() throws Exception{
		String input = "tos url u,\n integer x\n{integer y image i u -> i; i -> height; frame f i -> scale (x) -> f;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}

	@Test
	public void testBinaryExpression() throws Exception{
		String input = "p url z {\nimage x image y x <- x + (3*y);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}


}
