/* *
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites or repositories,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */

package cop5556fa19;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cop5556fa19.Scanner.LexicalException;

import static cop5556fa19.Token.Kind.*;

class ScannerTest {
	
	//I like this to make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	 /**
	  * Example showing how to get input from a Java string literal.
	  * 
	  * In this case, the string is empty.  The only Token that should be returned is an EOF Token.  
	  * 
	  * This test case passes with the provided skeleton, and should also pass in your final implementation.
	  * Note that calling getNext again after having reached the end of the input should just return another EOF Token.
	  * 
	  */
	@Test 
	void test0() throws Exception {
		Reader r = new StringReader("");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext()); 
		assertEquals(EOF, t.kind);
		show(t= s.getNext());
		assertEquals(EOF, t.kind);
	}

	/**
	 * Example showing how to create a test case to ensure that an exception is thrown when illegal input is given.
	 * 
	 * This "@" character is illegal in the final scanner (except as part of a String literal or comment). So this
	 * test should remain valid in your complete Scanner.
	 */
	@Test
	void testNULLinput() throws Exception {
		Reader r = new StringReader("@");
		Scanner s = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s.getNext();
        });
	}
	
	/**
	 * Example showing how to read the input from a file.  Otherwise it is the same as test1.
	 *
	 */
	@Test
	void testNULLFile() throws Exception {
		String file = "testInputFiles\\test2.input"; 
		Reader r = new BufferedReader(new FileReader(file));
		Scanner s = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s.getNext();
        });
	}
	

	
	/**
	 * Another example.  This test case will fail with the provided code, but should pass in your completed Scanner.
	 * @throws Exception
	 */
	@Test
	void testOPMulti() throws Exception {
		Reader r = new StringReader(",,::==;");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		show(t = s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		
		show(t = s.getNext());
		assertEquals(t.kind,COLONCOLON);
		assertEquals(t.text,"::");
		
		show(t = s.getNext());
		assertEquals(t.kind,REL_EQEQ);
		assertEquals(t.text,"==");
		
		show(t = s.getNext());
		assertEquals(t.kind,SEMI);
		assertEquals(t.text,";");
		assertEquals(t.pos,6);
		assertEquals(t.line,0);
		
	}
	/*Can distinguish single and double, eg: COLON vs COLONCOLON*/
	@Test
	void testOPSingle() throws Exception {
		Reader r = new StringReader(",,:=");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		show(t = s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		
		show(t = s.getNext());
		assertEquals(t.kind,COLON);
		assertEquals(t.text,":");
		
		show(t = s.getNext());
		assertEquals(t.kind,ASSIGN);
		assertEquals(t.text,"=");
	}
	
	/*test reline minor*/
	@Test
	void testSpacing() throws Exception {
		Reader r = new StringReader("\r\n,");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		assertEquals(t.pos,0);
		assertEquals(t.line,1);
		
	}
	
	/*test reline using normal \n*/
	@Test
	void testSpacingNormal() throws Exception {
		Reader r = new StringReader("\n,");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		assertEquals(t.pos,0);
		assertEquals(t.line,1);
		
	}
	
	/*test reline minor 2*/
	@Test
	void testSpacing1() throws Exception {
		Reader r = new StringReader("\r\n,\r\n,");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		assertEquals(t.pos,0);
		assertEquals(t.line,1);
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		assertEquals(t.pos,0);
		assertEquals(t.line,2);
	}
	
	/*test operations*/
	@Test
	void testOPGeneral() throws Exception {
		Reader r = new StringReader("+-*/%^#&~|(){}[]");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,OP_PLUS);
		assertEquals(t.text,"+");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,OP_MINUS);
		assertEquals(t.text,"-");
		assertEquals(t.pos,1);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,OP_TIMES);
		assertEquals(t.text,"*");
		assertEquals(t.pos,2);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,OP_DIV);
		assertEquals(t.text,"/");
		assertEquals(t.pos,3);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,OP_MOD);
		assertEquals(t.text,"%");
		assertEquals(t.pos,4);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,OP_POW);
		assertEquals(t.text,"^");
		assertEquals(t.pos,5);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,OP_HASH);
		assertEquals(t.text,"#");
		assertEquals(t.pos,6);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,BIT_AMP);
		assertEquals(t.text,"&");
		assertEquals(t.pos,7);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,BIT_XOR);
		assertEquals(t.text,"~");
		assertEquals(t.pos,8);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,BIT_OR);
		assertEquals(t.text,"|");
		assertEquals(t.pos,9);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,LPAREN);
		assertEquals(t.text,"(");
		assertEquals(t.pos,10);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,RPAREN);
		assertEquals(t.text,")");
		assertEquals(t.pos,11);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,LCURLY);
		assertEquals(t.text,"{");
		assertEquals(t.pos,12);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,RCURLY);
		assertEquals(t.text,"}");
		assertEquals(t.pos,13);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,LSQUARE);
		assertEquals(t.text,"[");
		assertEquals(t.pos,14);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,RSQUARE);
		assertEquals(t.text,"]");
		assertEquals(t.pos,15);
		assertEquals(t.line,0);
	}
	
	/*Test the situation of multiple ///*/
	@Test
	void testOPDuplication() throws Exception {
		Reader r = new StringReader("///////");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,OP_DIVDIV);
		assertEquals(t.text,"//");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,OP_DIVDIV);
		assertEquals(t.text,"//");
		assertEquals(t.pos,2);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,OP_DIVDIV);
		assertEquals(t.text,"//");
		assertEquals(t.pos,4);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,OP_DIV);
		assertEquals(t.text,"/");
		assertEquals(t.pos,6);
		assertEquals(t.line,0);
	}
	
	/*Test the situation of multiple /// with space*/
	@Test
	void testOPDuplicationWSP() throws Exception {
		Reader r = new StringReader("/ / /");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,OP_DIV);
		assertEquals(t.text,"/");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,OP_DIV);
		assertEquals(t.text,"/");
		assertEquals(t.pos,2);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,OP_DIV);
		assertEquals(t.text,"/");
		assertEquals(t.pos,4);
		assertEquals(t.line,0);
	}
	
	/*Test multiple branching from <,>*/
	@Test
	void testOPLES() throws Exception {
		Reader r = new StringReader("<=>=<><<>><<<");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,REL_LE);
		assertEquals(t.text,"<=");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,REL_GE);
		assertEquals(t.text,">=");
		assertEquals(t.pos,2);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,REL_LT);
		assertEquals(t.text,"<");
		assertEquals(t.pos,4);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,REL_GT);
		assertEquals(t.text,">");
		assertEquals(t.pos,5);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,BIT_SHIFTL);
		assertEquals(t.text,"<<");
		assertEquals(t.pos,6);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,BIT_SHIFTR);
		assertEquals(t.text,">>");
		assertEquals(t.pos,8);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,BIT_SHIFTL);
		assertEquals(t.text,"<<");
		assertEquals(t.pos,10);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,REL_LT);
		assertEquals(t.text,"<");
		assertEquals(t.pos,12);
		assertEquals(t.line,0);
	}
	
	/*Test keywords, need special case for testing*/
	@Test
	void testKeyWord() throws Exception {
		Reader r = new StringReader("and break do else end false for function goto if");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,KW_and);
		assertEquals(t.text,"and");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_break);
		assertEquals(t.text,"break");
		assertEquals(t.pos,4);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_do);
		assertEquals(t.text,"do");
		assertEquals(t.pos,10);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_else);
		assertEquals(t.text,"else");
		assertEquals(t.pos,13);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_end);
		assertEquals(t.text,"end");
		assertEquals(t.pos,18);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_false);
		assertEquals(t.text,"false");
		assertEquals(t.pos,22);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_for);
		assertEquals(t.text,"for");
		assertEquals(t.pos,28);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_function);
		assertEquals(t.text,"function");
		assertEquals(t.pos,32);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_goto);
		assertEquals(t.text,"goto");
		assertEquals(t.pos,41);
		assertEquals(t.line,0);
		show(t = s.getNext());
		assertEquals(t.kind,KW_if);
		assertEquals(t.text,"if");
		assertEquals(t.pos,46);
		assertEquals(t.line,0);
	}
	
	/*Test Naming, need special case for testing*/
	@Test
	void testNaming() throws Exception {
		Reader r = new StringReader("andwsadas asskda sif");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"andwsadas");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"asskda");
		assertEquals(t.pos,10);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"sif");
		assertEquals(t.pos,17);
		assertEquals(t.line,0);
	}
	
	/*Test INT, need special case for testing*/
	@Test
	void testINT() throws Exception {
		Reader r = new StringReader("12345678 123");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"12345678");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"123");
		assertEquals(t.pos,9);
		assertEquals(t.line,0);
	}
	
	/*Test INT, need special case for testing*/
	@Test
	void testINTExcep() throws Exception {
		Reader r = new StringReader("123456781213123123123223");
		Scanner s = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s.getNext();
        });
	}
	
	/*Test comment handling*/
	@Test
	void testSKIPCOMMENT() throws Exception {
		Reader r = new StringReader("--i am genius 213123\n xxx");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"xxx");
		assertEquals(t.pos,1);
		assertEquals(t.line,1);
		
		r = new StringReader("--i am genius 213123\r\n xxx");
		s = new Scanner(r);
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"xxx");
		assertEquals(t.pos,1);
		assertEquals(t.line,1);
	}
	
	/*Test STRINGLIT handling*/
	@Test
	void testStringLit() throws Exception{
		Reader r = new StringReader("\"hello\"\'hello\'");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,STRINGLIT);
		assertEquals(t.text,"\"hello\"");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,STRINGLIT);
		assertEquals(t.text,"\'hello\'");
		assertEquals(t.pos,7);
		assertEquals(t.line,0);
        
        r = new StringReader("\"hello\\r\\nabc\"+");
        Token t2;
        s = new Scanner(r);
		show(t2= s.getNext());
		assertEquals(t2.kind,STRINGLIT);
		assertEquals(t2.text,"\"hello\\r\\nabc\"");
		assertEquals(t2.pos,0);
		assertEquals(t2.line,0);
		
		show(t2= s.getNext());
		assertEquals(t2.kind,OP_PLUS);
		assertEquals(t2.text,"+");
		assertEquals(t2.pos,14);
		assertEquals(t2.line,0);
		
        r = new StringReader("\"hello\\nabc\"+");
        Token t3;
        s = new Scanner(r);
		show(t3= s.getNext());
		assertEquals(t3.kind,STRINGLIT);
		assertEquals(t3.text,"\"hello\\nabc\"");
		assertEquals(t3.pos,0);
		assertEquals(t3.line,0);
		
		show(t3= s.getNext());
		assertEquals(t3.kind,OP_PLUS);
		assertEquals(t3.text,"+");
		assertEquals(t3.pos,12);
		assertEquals(t3.line,0);
		
		r = new StringReader("\"hel\"lo\"");
		Scanner s2 = new Scanner(r);
		Token t4;
		show(t4 = s2.getNext());
		assertEquals(t4.kind,STRINGLIT);
		show(t4 = s2.getNext());
		assertEquals(t4.kind,NAME);
        assertThrows(LexicalException.class, ()->{
		   s2.getNext();
        });
        
		r = new StringReader("\"hel\'lo\"");
		Scanner s3 = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s3.getNext();
        });
        
		r = new StringReader("\"hello");
		Scanner s4 = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s4.getNext();
        });
        
        
		r = new StringReader("\"hel\\8989lo\"");
		Scanner s5 = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s5.getNext();
        });
        
		r = new StringReader("\"hel\nlo\" +");
		Scanner s6 = new Scanner(r);
		Token t5;
		show(t5= s6.getNext());
		assertEquals(t5.kind,STRINGLIT);
		assertEquals(t5.text,"\"hel\nlo\"");
		assertEquals(t5.pos,0);
		assertEquals(t5.line,0);
		
		show(t5= s6.getNext());
		assertEquals(t5.kind,OP_PLUS);
		assertEquals(t5.text,"+");
		assertEquals(t5.pos,4);
		assertEquals(t5.line,1);
	}
	
	/*Combination test, a normal input strings with all kinds of Token mixed*/
	@Test
	void testAll() throws Exception {
		Reader r = new StringReader("while I am a pig, 2345 -- dont read here\n 123 + 4 = 5"
				+ " Maybe \nand if elseif. \"Damage down piggy bobo\"\r\nYes");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,KW_while);
		assertEquals(t.text,"while");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"I");
		assertEquals(t.pos, 6);
		assertEquals(t.line, 0);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"am");
		assertEquals(t.pos,8);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"a");
		assertEquals(t.pos,11);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"pig");
		assertEquals(t.pos,13);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		assertEquals(t.pos,16);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"2345");
		assertEquals(t.pos,18);
		assertEquals(t.line,0);
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"123");
		assertEquals(t.pos,1);
		assertEquals(t.line,1);
		
		show(t= s.getNext());
		assertEquals(t.kind,OP_PLUS);
		assertEquals(t.text,"+");
		assertEquals(t.pos,5);
		assertEquals(t.line,1);
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"4");
		assertEquals(t.pos,7);
		assertEquals(t.line,1);
		
		show(t= s.getNext());
		assertEquals(t.kind,ASSIGN);
		assertEquals(t.text,"=");
		assertEquals(t.pos,9);
		assertEquals(t.line,1);
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"5");
		assertEquals(t.pos,11);
		assertEquals(t.line,1);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"Maybe");
		assertEquals(t.pos,13);
		assertEquals(t.line,1);
		
		show(t= s.getNext());
		assertEquals(t.kind,KW_and);
		assertEquals(t.text,"and");
		assertEquals(t.pos,0);
		assertEquals(t.line,2);
		
		show(t= s.getNext());
		assertEquals(t.kind,KW_if);
		assertEquals(t.text,"if");
		assertEquals(t.pos,4);
		assertEquals(t.line,2);
		
		show(t= s.getNext());
		assertEquals(t.kind,KW_elseif);
		assertEquals(t.text,"elseif");
		assertEquals(t.pos,7);
		assertEquals(t.line,2);
		
		show(t= s.getNext());
		assertEquals(t.kind,DOT);
		assertEquals(t.text,".");
		assertEquals(t.pos,13);
		assertEquals(t.line,2);
		
		show(t= s.getNext());
		assertEquals(t.kind,STRINGLIT);
		assertEquals(t.text,"\"Damage down piggy bobo\"");
		assertEquals(t.pos,15);
		assertEquals(t.line,2);
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"Yes");
		assertEquals(t.pos,0);
		assertEquals(t.line,3);	
	}
	
	/*TRY TO BREAK IT TEST,try to as confused as possible*/
	@Test
	void testToChallenge() throws Exception {
		Reader r = new StringReader("==== ~~= elseif elseinonoIamkidding\r\n01001 ... .. . 1+1=2 ......... abc\rcba");
		Token t;
		Scanner s = new Scanner(r);
		show(t= s.getNext());
		assertEquals(t.kind,REL_EQEQ);
		assertEquals(t.text,"==");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);	
		
		show(t= s.getNext());
		assertEquals(t.kind,REL_EQEQ);
		assertEquals(t.text,"==");
		assertEquals(t.pos,2);
		assertEquals(t.line,0);	
		
		show(t= s.getNext());
		assertEquals(t.kind,BIT_XOR);
		assertEquals(t.text,"~");
		assertEquals(t.pos,5);
		assertEquals(t.line,0);	
		
		show(t= s.getNext());
		assertEquals(t.kind,REL_NOTEQ);
		assertEquals(t.text,"~=");
		assertEquals(t.pos,6);
		assertEquals(t.line,0);	
		
		show(t= s.getNext());
		assertEquals(t.kind,KW_elseif);
		assertEquals(t.text,"elseif");
		assertEquals(t.pos,9);
		assertEquals(t.line,0);	
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"elseinonoIamkidding");
		assertEquals(t.pos,16);
		assertEquals(t.line,0);	
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"0");
		assertEquals(t.pos,0);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"1001");
		assertEquals(t.pos,1);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,DOTDOTDOT);
		assertEquals(t.text,"...");
		assertEquals(t.pos,6);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,DOTDOT);
		assertEquals(t.text,"..");
		assertEquals(t.pos,10);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,DOT);
		assertEquals(t.text,".");
		assertEquals(t.pos,13);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"1");
		assertEquals(t.pos,15);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,OP_PLUS);
		assertEquals(t.text,"+");
		assertEquals(t.pos,16);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"1");
		assertEquals(t.pos,17);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,ASSIGN);
		assertEquals(t.text,"=");
		assertEquals(t.pos,18);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,INTLIT);
		assertEquals(t.text,"2");
		assertEquals(t.pos,19);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,DOTDOTDOT);
		assertEquals(t.text,"...");
		assertEquals(t.pos,21);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,DOTDOTDOT);
		assertEquals(t.text,"...");
		assertEquals(t.pos,24);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,DOTDOTDOT);
		assertEquals(t.text,"...");
		assertEquals(t.pos,27);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"abc");
		assertEquals(t.pos,31);
		assertEquals(t.line,1);	
		
		show(t= s.getNext());
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"cba");
		assertEquals(t.pos,0);
		assertEquals(t.line,2);	
	}
	
	/*Test a sequence of char without space*/
	@Test
	void testNonSpaceExpression()  throws Exception{
		Reader r = new StringReader("abs+-ji\n\"hello\"\r\nits boring ");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext()); 
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"abs");
		assertEquals(t.pos,0);
		assertEquals(t.line,0);	
		
		show(t= s.getNext()); 
		assertEquals(t.kind,OP_PLUS);
		assertEquals(t.text,"+");
		assertEquals(t.pos,3);
		assertEquals(t.line,0);	
		
		show(t= s.getNext()); 
		assertEquals(t.kind,OP_MINUS);
		assertEquals(t.text,"-");
		assertEquals(t.pos,4);
		assertEquals(t.line,0);	
		
		show(t= s.getNext()); 
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"ji");
		assertEquals(t.pos,5);
		assertEquals(t.line,0);	
		
		show(t= s.getNext()); 
		assertEquals(t.kind,STRINGLIT);
		assertEquals(t.text,"\"hello\"");
		assertEquals(t.pos,0);
		assertEquals(t.line,1);	
		
		show(t= s.getNext()); 
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"its");
		assertEquals(t.pos,0);
		assertEquals(t.line,2);	
		
		show(t= s.getNext()); 
		assertEquals(t.kind,NAME);
		assertEquals(t.text,"boring");
		assertEquals(t.pos,4);
		assertEquals(t.line,2);	
	}
	
	
	
	
	

}
