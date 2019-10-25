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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import cop5556fa19.AST.ASTNode;
import cop5556fa19.AST.Block;
import cop5556fa19.AST.Chunk;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.FuncName;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.AST.RetStat;
import cop5556fa19.AST.Stat;
import cop5556fa19.AST.StatAssign;
import cop5556fa19.AST.StatBreak;
import cop5556fa19.AST.StatDo;
import cop5556fa19.AST.StatFor;
import cop5556fa19.AST.StatForEach;
import cop5556fa19.AST.StatFunction;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatLocalAssign;
import cop5556fa19.AST.StatLocalFunc;
import cop5556fa19.AST.StatRepeat;
import cop5556fa19.AST.StatWhile;
import cop5556fa19.Parser.SyntaxException;

class ParserTest {

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
	
	// creates a scanner, parser, and parses the input by calling exp().  
	Exp parseExpAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);
		Exp e = parser.exp();
		show("e=" + e);
		return e;
	}	
	
	
	// creates a scanner, parser, and parses the input by calling block()  
	Block parseBlockAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);
		Method method = Parser.class.getDeclaredMethod("block");
		method.setAccessible(true);
		Block b = (Block) method.invoke(parser);
		show("b=" + b);
		return b;
	}	
	
	
	//creates a scanner, parser, and parses the input by calling parse()
	//this corresponds to the actual use case of the parser
	Chunk parseAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);
		Chunk c = parser.parse();
		show("c="+c);
		return c;
	}
	
	@Test
	void testnil() throws Exception{
		String input = "nil";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpNil.class, e.getClass());
	}
	
	
	
	@Test
	
	void testVarArgs() throws Exception{
		String input = "...";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpVarArgs.class, e.getClass());
	}
	
	
	
	@Test
	void testfunctionhw2() throws Exception{
		String input = "function () end";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
	}
	@Test
	void testfunctionc() throws Exception {
		String input = "function (a,b,c,d,e,...) end";
		Exp e = parseExpAndShow(input);
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
		e = parseExpAndShow(input);
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
		e = parseExpAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
		test = new ArrayList<>();
		
		assertEquals(test, ((ExpFunction) e).body.p.nameList);
		assertEquals(true,((ExpFunction) e).body.p.hasVarArgs);
		
		input = "function (a) end";
		e = parseExpAndShow(input);
		assertEquals(ExpFunction.class, e.getClass());
		test = new ArrayList<>();
		test.add(new Name(new Token(Token.Kind.NAME,"a",0,0),"a"));
		
		assertEquals(test, ((ExpFunction) e).body.p.nameList);
		assertEquals(false,((ExpFunction) e).body.p.hasVarArgs);
	}
	
	@Test
	void testTableHw2() throws Exception{
		String input = "{}";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
	}
	
	
	@Test
	void testTable() throws Exception{	
		String input = "{[1 + 1] = 5 , X = 5, 45}";
		Exp e = parseExpAndShow(input);
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
		e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		e = parseExpAndShow(input);
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
		e = parseExpAndShow(input);
		assertEquals(ExpTable.class, e.getClass());
		x = new FieldExpKey(new Token(LSQUARE,"[",0,0),Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS, Expressions.makeInt(1)),Expressions.makeInt(5));
	
		test = new ArrayList<>();
		test.add(x);
		
		assertEquals(test, ((ExpTable) e).fields);
	}


	@Test
	void testIdent0() throws Exception {
		String input = "x";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testIdent1() throws Exception {
		String input = "(x)";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testString() throws Exception {
		String input = "\"string\"";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpString.class, e.getClass());
		assertEquals("string", ((ExpString) e).v);
	}

	@Test
	void testBoolean0() throws Exception {
		String input = "true";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpTrue.class, e.getClass());
	}

	@Test
	void testBoolean1() throws Exception {
		String input = "false";
		Exp e = parseExpAndShow(input);
		assertEquals(ExpFalse.class, e.getClass());
	}
	
	@Test
	void testBinaryor() throws Exception {
		String input = "1 or 2";
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeBinary(1,KW_or,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testBinary0() throws Exception {
		String input = "1 + 2";
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeBinary(1,OP_PLUS,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary0() throws Exception {
		String input = "-2";
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, 2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary1() throws Exception {
		String input = "-*2\n";
		assertThrows(SyntaxException.class, () -> {
		Exp e = parseExpAndShow(input);
		});	
	}
	

	
	@Test
	void testRightAssocDOTDOT() throws Exception {
		String input = "\"concat\" .. \"is\"..\"right associative\"";
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1), OP_TIMES, 
				Expressions.makeExpUnary(OP_MINUS, 2));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testprecedencePOWUNA() throws Exception {
		String input = "-2 ^ 5";
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, Expressions.makeBinary(Expressions.makeInt(2), OP_POW, Expressions.makeInt(5)));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssoc() throws Exception {
		String input = "\"minus\" - \"is\" + \"left associative\"";
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, OP_MINUS
				, Expressions.makeExpString("is")), OP_PLUS, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "-~\"is\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeExpUnary( OP_MINUS, 
				Expressions.makeExpUnary(BIT_XOR,Expressions.makeExpString("is")));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" or \"is\" or \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				KW_or, Expressions.makeExpString("is")), KW_or, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" and \"is\" and \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				KW_and, Expressions.makeExpString("is")), KW_and, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		
		input = "\"minus\" < \"is\" < \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				REL_LT, Expressions.makeExpString("is")), REL_LT, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" | \"is\" | \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_OR, Expressions.makeExpString("is")), BIT_OR, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" ~ \"is\" ~ \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_XOR, Expressions.makeExpString("is")), BIT_XOR, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" & \"is\" & \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_AMP, Expressions.makeExpString("is")), BIT_AMP, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" << \"is\" << \"left associative\"";
		e = parseExpAndShow(input);
		expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, 
				BIT_SHIFTL, Expressions.makeExpString("is")), BIT_SHIFTL, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "\"minus\" * \"is\" * \"left associative\"";
		e = parseExpAndShow(input);
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
		Exp e = parseExpAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(1), OP_PLUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "-(1+1)";
		e = parseExpAndShow(input);
		expected = Expressions.makeExpUnary(OP_MINUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
		input = "- - -(1+1)";
		e = parseExpAndShow(input);
		expected = Expressions.makeExpUnary(OP_MINUS,Expressions.makeExpUnary(OP_MINUS,Expressions.makeExpUnary(OP_MINUS,Expressions.makeBinary(
				Expressions.makeInt(1),OP_PLUS,Expressions.makeInt(1)))));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	
	///From this line, all tests are on Parsing 
	
	
	@Test
	void testEmpty1() throws Exception {
		String input = "";
		Block b = parseBlockAndShow(input);
		Block expected = Expressions.makeBlock();
		assertEquals(expected, b);
	}
	
	@Test
	void testEmpty2() throws Exception {
		String input = "";
		ASTNode n = parseAndShow(input);
		Block b = Expressions.makeBlock();
		Chunk expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
	}
	
	@Test
	void testAssign1() throws Exception {
		String input = "a=b";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(Expressions.makeExpName("a"));
		List<Exp> rhs = Expressions.makeExpList(Expressions.makeExpName("b"));
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,b);
	}
	
	@Test
	void testAssignChunk1() throws Exception {
		String input = "a=b";
		ASTNode c = parseAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(Expressions.makeExpName("a"));
		List<Exp> rhs = Expressions.makeExpList(Expressions.makeExpName("b"));
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block b = Expressions.makeBlock(s);
		Chunk expected = new Chunk(b.firstToken,b);
		assertEquals(expected,c);
	}
	

	@Test
	void testMultiAssign1() throws Exception {
		String input = "a,c=8,9";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(
					Expressions.makeExpName("a")
					,Expressions.makeExpName("c"));
		Exp e1 = Expressions.makeExpInt(8);
		Exp e2 = Expressions.makeExpInt(9);
		List<Exp> rhs = Expressions.makeExpList(e1,e2);
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,b);		
	}
	

	

	@Test
	void testMultiAssign3() throws Exception {
		String input = "a,c=8,f(x)";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(
					Expressions.makeExpName("a")
					,Expressions.makeExpName("c"));
		Exp e1 = Expressions.makeExpInt(8);
		List<Exp> args = new ArrayList<>();
		args.add(Expressions.makeExpName("x"));
		Exp e2 = Expressions.makeExpFunCall(Expressions.makeExpName("f"),args, null);
		List<Exp> rhs = Expressions.makeExpList(e1,e2);
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,b);			
	}
	

	
	@Test
	void testAssignToTable() throws Exception {
		String input = "g.a.b = 3";
		Block bl = parseBlockAndShow(input);
		ExpName g = Expressions.makeExpName("g");
		ExpString a = Expressions.makeExpString("a");
		Exp gtable = Expressions.makeExpTableLookup(g,a);
		ExpString b = Expressions.makeExpString("b");
		Exp v = Expressions.makeExpTableLookup(gtable, b);
		Exp three = Expressions.makeExpInt(3);		
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(three));;
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,bl);
	}
	
	@Test
	void testAssignTableToVar() throws Exception {
		String input = "x = g.a.b";
		Block bl = parseBlockAndShow(input);
		ExpName g = Expressions.makeExpName("g");
		ExpString a = Expressions.makeExpString("a");
		Exp gtable = Expressions.makeExpTableLookup(g,a);
		ExpString b = Expressions.makeExpString("b");
		Exp e = Expressions.makeExpTableLookup(gtable, b);
		Exp v = Expressions.makeExpName("x");		
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(e));;
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,bl);
	}
	

	
	@Test
	void testmultistatements6() throws Exception {
		String input = "x = g.a.b ; ::mylabel:: do  y = 2 goto mylabel f=a(0,200) end break"; //same as testmultistatements0 except ;
		ASTNode c = parseAndShow(input);
		ExpName g = Expressions.makeExpName("g");
		ExpString a = Expressions.makeExpString("a");
		Exp gtable = Expressions.makeExpTableLookup(g,a);
		ExpString b = Expressions.makeExpString("b");
		Exp e = Expressions.makeExpTableLookup(gtable, b);
		Exp v = Expressions.makeExpName("x");		
		Stat s0 = Expressions.makeStatAssign(v,e);
		StatLabel s1 = Expressions.makeStatLabel("mylabel");
		Exp y = Expressions.makeExpName("y");
		Exp two = Expressions.makeExpInt(2);
		Stat s2 = Expressions.makeStatAssign(y,two);
		Stat s3 = Expressions.makeStatGoto("mylabel");
		Exp f = Expressions.makeExpName("f");
		Exp ae = Expressions.makeExpName("a");
		Exp zero = Expressions.makeExpInt(0);
		Exp twohundred = Expressions.makeExpInt(200);
		List<Exp> args = Expressions.makeExpList(zero, twohundred);
		ExpFunctionCall fc = Expressions.makeExpFunCall(ae, args, null);		
		StatAssign s4 = Expressions.makeStatAssign(f,fc);
		StatDo statdo = Expressions.makeStatDo(s2,s3,s4);
		StatBreak statBreak = Expressions.makeStatBreak();
		Block expectedBlock = Expressions.makeBlock(s0,s1,statdo,statBreak);
		Chunk expectedChunk = new Chunk(expectedBlock.firstToken, expectedBlock);
		assertEquals(expectedChunk,c);
	}
	
	//
	@Test
	void testBlock() throws Exception{
		String input = "a=b; return";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(Expressions.makeExpName("a"));
		List<Exp> rhs = Expressions.makeExpList(Expressions.makeExpName("b"));
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		RetStat r = new RetStat(new Token(KW_return,"return",0,0),null);
		Block expected = Expressions.makeBlock(s,r);
		assertEquals(expected,b);
		
		input = "return a";
		b = parseBlockAndShow(input);	
		List<Exp> exps = new ArrayList<>();
		exps.add(new ExpName(new Token(NAME,"a",0,0)));
		r = new RetStat(new Token(KW_return,"return",0,0),exps);
		expected = Expressions.makeBlock(r);
		assertEquals(expected,b);
		
		input = "a=b; return a";
		b = parseBlockAndShow(input);		
	    lhs = Expressions.makeExpList(Expressions.makeExpName("a"));
		rhs = Expressions.makeExpList(Expressions.makeExpName("b"));
		s = Expressions.makeStatAssign(lhs,rhs);
		exps = new ArrayList<>();
		exps.add(new ExpName(new Token(NAME,"a",0,0)));
		r = new RetStat(new Token(KW_return,"return",0,0),exps);
		expected = Expressions.makeBlock(s,r);
		assertEquals(expected,b);
		
		input = "a=b; return a;";
		b = parseBlockAndShow(input);		
	    lhs = Expressions.makeExpList(Expressions.makeExpName("a"));
		rhs = Expressions.makeExpList(Expressions.makeExpName("b"));
		s = Expressions.makeStatAssign(lhs,rhs);
		exps = new ArrayList<>();
		exps.add(new ExpName(new Token(NAME,"a",0,0)));
		r = new RetStat(new Token(KW_return,"return",0,0),exps);
		expected = Expressions.makeBlock(s,r);
		assertEquals(expected,b);
		
		input = "a=b; return a,b;";
		b = parseBlockAndShow(input);		
	    lhs = Expressions.makeExpList(Expressions.makeExpName("a"));
		rhs = Expressions.makeExpList(Expressions.makeExpName("b"));
		s = Expressions.makeStatAssign(lhs,rhs);
		exps = new ArrayList<>();
		exps.add(new ExpName(new Token(NAME,"a",0,0)));
		exps.add(new ExpName(new Token(NAME,"b",0,0)));
		r = new RetStat(new Token(KW_return,"return",0,0),exps);
		expected = Expressions.makeBlock(s,r);
		assertEquals(expected,b);
	}
	
	@Test
	void testStatFromTopToBot() throws Exception{
		String input = ";";
		ASTNode n = parseAndShow(input);
		Block b = Expressions.makeBlock();
		Chunk expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
		
		input = "::a::";
		n = parseAndShow(input);		
		Stat e = Expressions.makeStatLabel("a") ;
		b = Expressions.makeBlock(e);
		expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
		
		input = "break";
		n = parseAndShow(input);		
		e = Expressions.makeStatBreak() ;
		b = Expressions.makeBlock(e);
		expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
		
		input = "goto b";
		n = parseAndShow(input);		
		e = Expressions.makeStatGoto("b") ;
		b = Expressions.makeBlock(e);
		expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
		
		input = "do ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatDo(Expressions.makeStatLabel("a"));
		b = Expressions.makeBlock(e);
		expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
		
		input = "while true do ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		Stat w = new StatWhile(new Token(KW_while,"while",0,0),new ExpTrue(new Token(KW_true,"true",0,0)),b);
		Block b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "repeat ::a:: until true";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		w = new StatRepeat(new Token(KW_repeat,"repeat",0,0),b,new ExpTrue(new Token(KW_true,"true",0,0)));
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "if true then ::a:: end";
		n = parseAndShow(input);		
		List<Exp> cods = new ArrayList<>();
		cods.add(new ExpTrue(new Token(KW_true,"true",0,0)));
		List<Block> bs = new ArrayList<>();
		bs.add(Expressions.makeBlock(Expressions.makeStatLabel("a")));
		w = new StatIf(new Token(KW_if,"if",0,0),cods,bs);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "if true then ::a:: elseif abc then ::b:: end";
		n = parseAndShow(input);		
		cods = new ArrayList<>();
		cods.add(new ExpTrue(new Token(KW_true,"true",0,0)));
		bs = new ArrayList<>();
		bs.add(Expressions.makeBlock(Expressions.makeStatLabel("a")));
		cods.add(Expressions.makeExpName("abc"));
		bs.add(Expressions.makeBlock(Expressions.makeStatLabel("b")));
		w = new StatIf(new Token(KW_if,"if",0,0),cods,bs);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "if true then ::a:: else ::b:: end";
		n = parseAndShow(input);		
		cods = new ArrayList<>();
		cods.add(new ExpTrue(new Token(KW_true,"true",0,0)));
		bs = new ArrayList<>();
		bs.add(Expressions.makeBlock(Expressions.makeStatLabel("a")));
		bs.add(Expressions.makeBlock(Expressions.makeStatLabel("b")));
		w = new StatIf(new Token(KW_if,"if",0,0),cods,bs);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "for a = 1 , nil do ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		w = new StatFor(new Token(KW_for,"for",0,0),Expressions.makeExpName("a"),Expressions.makeInt(1),new ExpNil(new Token(KW_nil,"nil",0,0)),null,b);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "for a = 1 , nil,nil do ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		w = new StatFor(new Token(KW_for,"for",0,0),Expressions.makeExpName("a"),Expressions.makeInt(1),new ExpNil(new Token(KW_nil,"nil",0,0)),new ExpNil(new Token(KW_nil,"nil",0,0)),b);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		
		input = "for a,b,c in nil,nil,nil do ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		List<ExpName> namlist = new ArrayList<>();
		List<Exp> explist = new ArrayList<>();
		namlist.add(Expressions.makeExpName("a"));
		namlist.add(Expressions.makeExpName("b"));
		namlist.add(Expressions.makeExpName("c"));
		explist.add(new ExpNil(new Token(KW_nil,"nil",0,0)));
		explist.add(new ExpNil(new Token(KW_nil,"nil",0,0)));
		explist.add(new ExpNil(new Token(KW_nil,"nil",0,0)));
	
		w = new StatForEach(new Token(KW_for,"for",0,0),namlist,explist,b);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		input = "function x () ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		List<ExpName> names = new ArrayList<>();
		names.add(new ExpName(new Token(NAME,"x",0,0)));
		FuncName fn = new FuncName(new Token(NAME,"x",0,0),names,null);
		FuncBody fb = new FuncBody(new Token(COLONCOLON,"::",0,0),null,b);
		w = new StatFunction(new Token(KW_function,"function",0,0),fn,fb);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		
		
		
		input = "local function x () ::a:: end";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		names = new ArrayList<>();
		names.add(new ExpName(new Token(NAME,"x",0,0)));
		fn = new FuncName(new Token(NAME,"x",0,0),names,null);
		fb = new FuncBody(new Token(COLONCOLON,"::",0,0),null,b);
		w = new StatLocalFunc(new Token(KW_local,"function",0,0),fn,fb);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		
		input = "local a,b,c = nil,nil,nil";
		n = parseAndShow(input);		
		e = Expressions.makeStatLabel("a");
		b = Expressions.makeBlock(e);
		names = new ArrayList<>();
		names.add(new ExpName(new Token(NAME,"a",0,0)));
		names.add(new ExpName(new Token(NAME,"b",0,0)));
		names.add(new ExpName(new Token(NAME,"c",0,0)));
		
		List<Exp> varlist = new ArrayList<>();
		varlist.add(new ExpNil(new Token(KW_nil,"nil",0,0)));
		varlist.add(new ExpNil(new Token(KW_nil,"nil",0,0)));
		varlist.add(new ExpNil(new Token(KW_nil,"nil",0,0)));
		
		w = new StatLocalAssign(new Token(KW_local,"function",0,0),names,varlist);
		b2 = Expressions.makeBlock(w);
		expected = new Chunk(b2.firstToken,b2);
		assertEquals(expected,n);
		
		
	}
	
	@Test
	
	void spotCheck() throws Exception{
		
	}
	
	
}
