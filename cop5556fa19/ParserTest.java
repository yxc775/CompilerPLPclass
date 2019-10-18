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
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.Parser.SyntaxException;

class ParserTest {

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
		Parser parser = new Parser(scanner);  // Create a parser
		Exp e = parser.exp(); // Parse and expression
		show("e=" + e);  //Show the resulting AST
		return e;
	}
	
	@Test
	void testnil() throws Exception{
		String input = "nil";
		Exp e = parseAndShow(input);
		assertEquals(ExpNil.class, e.getClass());
	}
	
	@Test
	
	void testVarArgs() throws Exception{
		String input = "...";
		Exp e = parseAndShow(input);
		assertEquals(ExpVarArgs.class, e.getClass());
	}
	
	
	
	@Test
	void testfunctionhw2() throws Exception{
		String input = "function () end";
		Exp e = parseAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
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
		
		input = "function (...) end";
		e = parseAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
		test = new ArrayList<>();
		
		assertEquals(test, ((ExpFunction) e).body.p.nameList);
		assertEquals(true,((ExpFunction) e).body.p.hasVarArgs);
		
		input = "function (a) end";
		e = parseAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
		test = new ArrayList<>();
		test.add(new Name(new Token(Token.Kind.NAME,"a",0,0),"a"));
		
		assertEquals(test, ((ExpFunction) e).body.p.nameList);
		assertEquals(false,((ExpFunction) e).body.p.hasVarArgs);
	}
	
	@Test
	void testTableHw2() throws Exception{
		String input = "{}";
		Exp e = parseAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
	}
	
	@Test
	void testTable() throws Exception{	
		String input = "{[1 + 1] = 5 , X = 5, 45}";
		Exp e = parseAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
		FieldExpKey x = new FieldExpKey(new Token(LSQUARE,"[",0,0),Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS, Expressions.makeInt(1)),Expressions.makeInt(5));
		FieldNameKey y = new FieldNameKey(new Token(NAME, "X",0,0),new Name(new Token(NAME, "X",0,0),"X"),Expressions.makeInt(5));
		FieldImplicitKey z = new FieldImplicitKey(new Token(INTLIT,"45",0,0),Expressions.makeInt(45));
		
		List<Field> test = new ArrayList<>();
		test.add(x);
		test.add(y);
		test.add(z);
		
		assertEquals(test, ((ExpTable) e).fields);
		
		input = "{[1 + 1] = 5 , X = 5, 45,}";
		e = parseAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
		x = new FieldExpKey(new Token(LSQUARE,"[",0,0),Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS, Expressions.makeInt(1)),Expressions.makeInt(5));
		y = new FieldNameKey(new Token(NAME, "X",0,0),new Name(new Token(NAME, "X",0,0),"X"),Expressions.makeInt(5));
		z = new FieldImplicitKey(new Token(INTLIT,"45",0,0),Expressions.makeInt(45));
		
	    test = new ArrayList<>();
		test.add(x);
		test.add(y);
		test.add(z);
		
		assertEquals(test, ((ExpTable) e).fields);
	}
	
	@Test
	void testSpecialTable() throws Exception{
		String input = "{[1 + 1] = 5 , X = 5, X , 45 , X + 5}";
		Exp e = parseAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
		FieldExpKey x = new FieldExpKey(new Token(LSQUARE,"[",0,0),Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS, Expressions.makeInt(1)),Expressions.makeInt(5));
		FieldNameKey y = new FieldNameKey(new Token(NAME, "X",0,0),new Name(new Token(NAME, "X",0,0),"X"),Expressions.makeInt(5));
		FieldImplicitKey y2 = new FieldImplicitKey(new Token(NAME,"X",0,0),new ExpName(new Token(NAME,"X",0,0)));
		FieldImplicitKey z = new FieldImplicitKey(new Token(INTLIT,"45",0,0),Expressions.makeInt(45));
		FieldImplicitKey z2 = new FieldImplicitKey(new Token(NAME,"X",0,0),Expressions.makeBinary(new ExpName(new Token(NAME,"X",0,0)), OP_PLUS, Expressions.makeInt(5)));
		
		List<Field> test = new ArrayList<>();
		test.add(x);
		test.add(y);
		test.add(y2);
		test.add(z);
		test.add(z2);
		
		assertEquals(test, ((ExpTable) e).fields);
		
		input = "{[1 + 1] = 5 , X = 5, X , 45 , X + 5};";
		e = parseAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
		x = new FieldExpKey(new Token(LSQUARE,"[",0,0),Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS, Expressions.makeInt(1)),Expressions.makeInt(5));
		y = new FieldNameKey(new Token(NAME, "X",0,0),new Name(new Token(NAME, "X",0,0),"X"),Expressions.makeInt(5));
		y2 = new FieldImplicitKey(new Token(NAME,"X",0,0),new ExpName(new Token(NAME,"X",0,0)));
		z = new FieldImplicitKey(new Token(INTLIT,"45",0,0),Expressions.makeInt(45));
		z2 = new FieldImplicitKey(new Token(NAME,"X",0,0),Expressions.makeBinary(new ExpName(new Token(NAME,"X",0,0)), OP_PLUS, Expressions.makeInt(5)));
		
		test = new ArrayList<>();
		test.add(x);
		test.add(y);
		test.add(y2);
		test.add(z);
		test.add(z2);
		
		assertEquals(test, ((ExpTable) e).fields);
		
		input = "{[1 + 1] = 5};";
		e = parseAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
		x = new FieldExpKey(new Token(LSQUARE,"[",0,0),Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS, Expressions.makeInt(1)),Expressions.makeInt(5));
	
		test = new ArrayList<>();
		test.add(x);
		
		assertEquals(test, ((ExpTable) e).fields);
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
	void testRightAssocDOTDOT() throws Exception {
		String input = "\"concat\" .. \"is\"..\"right associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeExpString("concat")
				, DOTDOT
				, Expressions.makeBinary("is",DOTDOT,"right associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testRightAssocPOW() throws Exception {
		String input = "\"concat\" ^ \"is\" ^ \"right associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeExpString("concat")
				, OP_POW
				, Expressions.makeBinary("is",OP_POW,"right associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedenceOrAnd() throws Exception {
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
	void testprecedenceCompareAnd() throws Exception {
		String input = "1 and 2 < 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), KW_and, 
					Expressions.makeBinary(
						Expressions.makeInt(2), REL_LT, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testprecedenceorCompare() throws Exception {
		String input = "1 < 2 | 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), REL_LT, 
					Expressions.makeBinary(
						Expressions.makeInt(2), BIT_OR, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testprecedenceXoror() throws Exception {
		String input = "1 | 2 ~ 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), BIT_OR, 
					Expressions.makeBinary(
						Expressions.makeInt(2), BIT_XOR, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testprecedenceAmpXor() throws Exception {
		String input = "1 ~ 2 & 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), BIT_XOR, 
					Expressions.makeBinary(
						Expressions.makeInt(2), BIT_AMP, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedenceShiftAmp() throws Exception {
		String input = "1 & 2 >> 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), BIT_AMP, 
					Expressions.makeBinary(
						Expressions.makeInt(2), BIT_SHIFTR, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedenceDOTDOTShift() throws Exception {
		String input = "1 >> 2 .. 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), BIT_SHIFTR, 
					Expressions.makeBinary(
						Expressions.makeInt(2), DOTDOT, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedencePLUSDOTDOT() throws Exception {
		String input = "1 .. 2 + 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), DOTDOT, 
					Expressions.makeBinary(
						Expressions.makeInt(2), OP_PLUS, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedenceTIMESPLUS() throws Exception {
		String input = "1 + 2 * 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), OP_PLUS, 
					Expressions.makeBinary(
						Expressions.makeInt(2), OP_TIMES, Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedenceUNATIMES() throws Exception {
		String input = "1 * -2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), OP_TIMES, 
				Expressions.makeExpUnary(OP_MINUS, 2));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedencePOWUNA() throws Exception {
		String input = "-2 ^ 5";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, Expressions.makeBinary(Expressions.makeInt(2), OP_POW, Expressions.makeInt(5)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssoc() throws Exception {
		String input = "\"minus\" - \"is\" + \"left associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, OP_MINUS
				, Expressions.makeExpString("is")), OP_PLUS, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "-~\"is\"";
		e = parseAndShow(input);
		expected = Expressions.makeExpUnary( OP_MINUS, 
				Expressions.makeExpUnary(BIT_XOR,Expressions.makeExpString("is")));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" or \"is\" or \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				KW_or, Expressions.makeExpString("is")), KW_or, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" and \"is\" and \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				KW_and, Expressions.makeExpString("is")), KW_and, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		
		input = "\"minus\" < \"is\" < \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				REL_LT, Expressions.makeExpString("is")), REL_LT, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" | \"is\" | \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_OR, Expressions.makeExpString("is")), BIT_OR, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" ~ \"is\" ~ \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_XOR, Expressions.makeExpString("is")), BIT_XOR, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" & \"is\" & \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_AMP, Expressions.makeExpString("is")), BIT_AMP, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" << \"is\" << \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_SHIFTL, Expressions.makeExpString("is")), BIT_SHIFTL, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" * \"is\" * \"left associative\"";
		e = parseAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				OP_TIMES, Expressions.makeExpString("is")), OP_TIMES, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testParenAssoc() throws Exception {
		String input = "1+(1+1)";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "-(1+1)";
		e = parseAndShow(input);
		expected = Expressions.makeExpUnary(OP_MINUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "- - -(1+1)";
		e = parseAndShow(input);
		expected = Expressions.makeExpUnary(OP_MINUS,Expressions.makeExpUnary(OP_MINUS,Expressions.makeExpUnary(OP_MINUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)))));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
}
