/* *
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */


package cop5556fa19;

import static cop5556fa19.Token.Kind.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.ExpressionParser.SyntaxException;

class ExpressionParserTest {

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	
	// creates a scanner, parser, and parses the input.  
	Exp parseAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		ExpressionParser parser = new ExpressionParser(scanner);  // Create a parser
		Exp e = parser.exp(); // Parse and expression
		show("e=" + e);  //Show the resulting AST
		return e;
	}
	
	@Test
	void testfunctionc() throws Exception {
		String input = "function (a,b,c,d,e,...) end";
		Exp e = parseAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
		List<Name> test = new ArrayList<>();
		test.add(new Name(new Token(Token.Kind.NAME,"a",0,0),"a"));
		test.add(new Name(new Token(Token.Kind.NAME,"b",0,0),"b"));
		test.add(new Name(new Token(Token.Kind.NAME,"c",0,0),"c"));
		test.add(new Name(new Token(Token.Kind.NAME,"d",0,0),"d"));
		test.add(new Name(new Token(Token.Kind.NAME,"e",0,0),"e"));
		
		assertEquals(test, ((ExpFunction) e).body.p.nameList);
		assertEquals(true,((ExpFunction) e).body.p.hasVarArgs);
		
		input = "function (a,b,c,d,e) end";
		e = parseAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
		test = new ArrayList<>();
		test.add(new Name(new Token(Token.Kind.NAME,"a",0,0),"a"));
		test.add(new Name(new Token(Token.Kind.NAME,"b",0,0),"b"));
		test.add(new Name(new Token(Token.Kind.NAME,"c",0,0),"c"));
		test.add(new Name(new Token(Token.Kind.NAME,"d",0,0),"d"));
		test.add(new Name(new Token(Token.Kind.NAME,"e",0,0),"e"));
		
		assertEquals(test, ((ExpFunction) e).body.p.nameList);
		assertEquals(false,((ExpFunction) e).body.p.hasVarArgs);
	}


	@Test
	void testIdent0() throws Exception {
		String input = "x";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testIdent1() throws Exception {
		String input = "(x)";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testString() throws Exception {
		String input = "\"string\"";
		Exp e = parseAndShow(input);
		assertEquals(ExpString.class, e.getClass());
		assertEquals("string", ((ExpString) e).v);
	}

	@Test
	void testBoolean0() throws Exception {
		String input = "true";
		Exp e = parseAndShow(input);
		assertEquals(ExpTrue.class, e.getClass());
	}

	@Test
	void testBoolean1() throws Exception {
		String input = "false";
		Exp e = parseAndShow(input);
		assertEquals(ExpFalse.class, e.getClass());
	}
	
	@Test
	void testBinaryor() throws Exception {
		String input = "1 or 2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(1,KW_or,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testBinary0() throws Exception {
		String input = "1 + 2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(1,OP_PLUS,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary0() throws Exception {
		String input = "-2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, 2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary1() throws Exception {
		String input = "-*2\n";
		assertThrows(SyntaxException.class, () -> {
		Exp e = parseAndShow(input);
		});	
	}
	

	
	@Test
	void testRightAssoc() throws Exception {
		String input = "\"concat\" .. \"is\"..\"right associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeExpString("\"concat\"")
				, DOTDOT
				, Expressions.makeBinary("\"is\"",DOTDOT,"\"right associative\""));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedence() throws Exception {
		String input = "1 or 2 and 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), KW_or, 
					Expressions.makeBinary(
						Expressions.makeInt(2), KW_and, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testLeftAssoc() throws Exception {
		String input = "\"minus\" - \"is\" - \"left associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("\"minus\"")
				, OP_MINUS
				, Expressions.makeExpString("\"is\"")), OP_MINUS, 
				Expressions.makeExpString("\"left associative\""));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testParenAssoc() throws Exception {
		String input = "1 +(1+1)";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
}
