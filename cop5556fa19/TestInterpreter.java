package cop5556fa19;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import cop5556fa19.Parser.SyntaxException;
import interpreter.ASTVisitorAdapter;
import interpreter.Interpreter;
import interpreter.LuaBoolean;
import interpreter.LuaInt;
import interpreter.LuaNil;
import interpreter.LuaString;
import interpreter.LuaTable;
import interpreter.LuaValue;
import interpreter.StaticSemanticException;

	class TestInterpreter{

		// To make it easy to print objects and turn this output on and off
		static final boolean doPrint = true;
//		static final boolean doPrint = false;

		private void show(Object input) {
			if (doPrint) {
				System.out.println(input);
			}
		}
				
		/**
		 * scans, parses, and interprets a program representing a Lua chunk.
		 * 
		 * @param input  String containing program source code
		 * @return  a (possbily empty) list of  LuaValue objects.
		 * 
		 * @throws Exception
		 * 
		 *Exceptions may be thrown for various static or dynamic errors
		 */
		
		List<LuaValue> interpret(String input) throws Exception{
			ASTVisitorAdapter lua = new Interpreter();
			Reader r = new StringReader(input);
			List<LuaValue> ret = (List<LuaValue>) lua.load(r);	
			return ret;
		}
		
		/**
		 * Utility function for tests. The interpret function may return a List<LuaValue>
		 * whose contents may be compared with expected using assertions.  This function 
		 * helps construct the "expected" object.
		 * 
		 * @param v  variable length list of ints
		 * @return   List<LuaValue> with value corresponding to input params
		 * 
		 */
		List<LuaValue> makeExpectedWithInts(int ... v){
			List<LuaValue> l = new ArrayList<>();
			for (int i: v) {
				l.add(new LuaInt(i));
			}
			return l;
		}
		
		
		@Test
		void runEmpty() throws Exception{
			String input = "";
			List<LuaValue> ret = interpret(input);
			List<LuaValue> expected = null;
			assertEquals(expected,ret);
		}
		
		@Test
		void run1() throws Exception{
			String input = "return 42";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			LuaValue[] vals = {new LuaInt(42)};
			List<LuaValue> expected = Arrays.asList(vals);
			assertEquals(expected, ret);
		}
		
		@Test
		void run2() throws Exception{
			String input = "x=35 return x";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			LuaValue[] vals = {new LuaInt(35)};
			List<LuaValue> expected = Arrays.asList(vals);
			assertEquals(expected, ret);
		}
			
		@Test
		void fail_run2returns() throws Exception{
			String input = "return 42; return 53;";
			show(input);
			assertThrows(SyntaxException.class,()->{
				List<LuaValue> ret = interpret(input);
				show(ret);
			});		
		}
		
		
		@Test
		void run3() throws Exception{
			String input = "do return 42 end return 53 ";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(42);
			assertEquals(expected, ret);
		}
		

		
		@Test 
		void if0() throws Exception {
			String input = "if true then x=3 end return x";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(3);
			assertEquals(expected, ret);
		}
		
		@Test 
		void if1() throws Exception {
			String input = "if false then x=3 end return x";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>();
			expected.add(LuaNil.nil);
			assertEquals(expected, ret);
		}
		
		@Test 
		void ifnilIsFalse() throws Exception {
			String input = "if x then x=3 end return x";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>();
			expected.add(LuaNil.nil);
			assertEquals(expected, ret);
		}
		
		@Test 
		void ifzeroistrue() throws Exception {
			String input = "if 0 then x=3 end return x";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(3);
			assertEquals(expected, ret);
		}
		
		@Test 
		void if2() throws Exception {
			String input = "if x then x=3 elseif y then y=4 elseif true then x=10 else y=11 end return x,y";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>();
			expected.add(LuaInt.valueOf(10));
			expected.add(LuaNil.nil);
			assertEquals(expected, ret);
		}
		
		@Test 
		void fail_ifgoto() throws Exception {
			String input = "y = 0 if x then x=3 elseif y then y=4 goto label1 elseif true then ::label1:: x=10  else y=11 end z = 12 y=20 return x,y,z";
			show(input);
			assertThrows(StaticSemanticException.class,()->{
				List<LuaValue> ret = interpret(input);
			});		
		}
		
		@Test 
		void if3() throws Exception {
			String input = "if x then xi=3 elseif y then y=4 elseif true then x=10 goto label1 else y=11 end z = 12 ::label1:: y=20 return x,y,z";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>();
			expected.add(LuaInt.valueOf(10));
			expected.add(LuaInt.valueOf(20));
			expected.add(LuaNil.nil);
			assertEquals(expected, ret);
		}
		
		@Test 
		void goto0() throws Exception {
			String input = "if x then x=3 elseif y then y=4 elseif true then do x=10 goto label1 end else y=11 end z = 12 ::label1:: y=20 return x,y,z";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>();
			expected.add(LuaInt.valueOf(10));
			expected.add(LuaInt.valueOf(20));
			expected.add(LuaNil.nil);
			assertEquals(expected, ret);
		}
		
		@Test 
		void gotoscopedlabels1() throws Exception {
			String input = "x = 0 "
					+ "\nif x "
					+ "\n  then x=3 "
					+ "\n  elseif y "
					+ "\n    then y=4 "
					+ "\n    elseif true "
					+ "\n      then do x=10 goto label1 end "
					+ "\n      else y=11 "
					+ "\nend "
					+ "\nz = 12 "
					+ "\n::label1:: "
					+ "\ny=20 "
					+ "\nreturn x,y,z";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(3,20,12);
			assertEquals(expected, ret);
		}
		
		@Test 
		void fail_gotoscopedlabels() throws Exception { //missing label
			String input = "if x "
					+ "\n  then x=3 "
					+ "\n  elseif y "
					+ "\n    then y=4 "
					+ "\n    elseif true "
					+ "\n      then do x=10 goto label1 end "
					+ "\n      else y=11 "
					+ "\nend "
					+ "\nz = 12 "
					+ "\ny=20 "
					+ "\nreturn x,y,z";
			show(input);
			assertThrows(StaticSemanticException.class,()->{
				List<LuaValue> ret = interpret(input);
			});		
		}
		
		@Test 
		void gotoscopedlabels2() throws Exception { 
			String input = "do x=1 do y=2 do a = 4 goto label1 b=5 ::label1:: z=3 end ::label1:: w=6 end end return w,x,y,z,a,b";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(6,1,2,3,4,5);
			expected.set(5,LuaNil.nil);
			assertEquals(expected, ret);			
		}
		
		@Test 
		void fail_gotoscopedlabels2() throws Exception { 
			String input = "do x=1 do y=2 do a = 4 goto label1 b=5  z=3 end ::label1:: w=6 end end return w,x,y,z,a,b";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(6,1,2,3,4,5);
			expected.set(3,LuaNil.nil);
			expected.set(5,LuaNil.nil);
			assertEquals(expected, ret);			
		}
		
		@Test
		void while0() throws Exception {
			String input = "i = 5  sum = 0 while i>0 do dummy=print(i) dummy=println(\",\") sum = sum + i   i=i-1 end return sum,i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(15,0);
			assertEquals(expected,ret);
		}
		
		@Test
		void whileDoubleWhile() throws Exception {
			String input = "i = 5 j = 2 sum = 0 while j > 0 do while i>0 do sum = sum + i  i=i-1 end j = j-1 end return j,i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(0,0);
			assertEquals(expected,ret);
		}
		
		
		@Test
		void break0() throws Exception {
			String input = "x=1 do x=2 do x=3 do break x=4 y0=0 end y1=1 end y2=2 end return x,y0,y1,y2 ";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);		
			List<LuaValue> expected = new ArrayList<>();
			expected.add(LuaInt.valueOf(3));
			expected.add(LuaNil.nil);
			expected.add(LuaInt.valueOf(1));
			expected.add(LuaInt.valueOf(2));			
			assertEquals(expected, ret);
		}
		
		@Test
		void whilebreak0() throws Exception {
			String input = "i = 10  "
					+ "\nsum = 0 "
					+ "\nwhile i>0 "
					+ "\ndo if i < 4 "
					+ "\n   then break end"
					+ "\ndummy=println(i) "
					+ "\nsum = sum + i   "
					+ "\ni=i-1 "
					+ "\n end "
					+ "\n dummy=println(\"end of loop\")"
					+ "\nreturn sum,i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(49,3);
			assertEquals(expected,ret);			
		}
		
		@Test
		void doubleDoWhileBreak0() throws Exception {
			String input = "i = 0\r\n" + 
					"while i < 5 do do if i > 2 then break end i = i + 1 end end\r\n" + 
					"return i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(3);
			assertEquals(expected,ret);			
		}
		
		
		@Test
		void whileDoubleWhileBreak() throws Exception {
			String input = "i = 5 j = 2 sum = 0 while j > 0 do while i>0 do if i < 4 then break end sum = sum + i  i=i-1 end j = j-1 end return j,i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(0,3);
			assertEquals(expected,ret);
		}
		
		@Test
		void repeat0() throws Exception {
			String input = "i = 0;" + 
					"repeat i = i + 1 until i > 5" + 
					"return i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(6);
			assertEquals(expected,ret);
		}
		
		@Test
		void repeat0break() throws Exception {
			String input = "i = 0;" + 
					"\nrepeat i = i + 1" + 
					"\nif i > 3 then break end" + 
					"\nuntil i > 5" +
					"\nreturn i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(4);
			assertEquals(expected,ret);
		}
		
		@Test
		void repeatWhilemixbreak() throws Exception {
			String input = "i = 0;\r\n" + 
					"j = 0;\r\n" + 
					"while j < 2\r\n" + 
					"do\r\n" + 
					"\r\n" + 
					"repeat i = i + 1 \r\n" + 
					"if i > 3 then break end\r\n" + 
					"until i > 5\r\n" + 
					"j = j + 1\r\n" + 
					"end\r\n" + 
					"return i,j";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(5,2);
			assertEquals(expected,ret);
		}
		
		@Test
		void repeat0Doublebreak() throws Exception {
			String input = "i = 0;\r\n" + 
					"j = 0;\r\n" + 
					"repeat j = j + 1\r\n" + 
					"repeat i = i + 1 \r\n" + 
					"if i > 3 then break end\r\n" + 
					"until i > 5\r\n" + 
					"until j > 2\r\n" + 
					"return i,j";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(6,3);
			assertEquals(expected,ret);
		}
		
		@Test
		void whilebreak1() throws Exception {
			String input = "i = 10  "
					+ "\nsum = 0 "
					+ "\nwhile i>0 "
					+ "\ndo if i < 4 "
					+ "\n   then do break end end"  //should break out of do, not just enclosing block
					+ "\ndummy=println(i) "
					+ "\nsum = sum + i   "
					+ "\ni=i-1 "
					+ "\n end "
					+ "\n dummy=println(\"end of loop\")"
					+ "\nreturn sum,i";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = makeExpectedWithInts(49,3);
			assertEquals(expected,ret);			
		}	
		
		
		@Test
		void table5() throws Exception {
			String input = ""
					+ "f1 = 777"
					+ "\na = { [f1] = g, "
					+ "\n\"x\","
					+ "\n--\"y\", "
					+ "\nx = 1, "
					+ "\nf1, "
					+ "\n[30] = 23, "
					+ "\n45 } "
					+ "\nreturn a";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.putImplicit(new LuaString("x"));
			expected.putImplicit(new LuaInt(777));
			expected.putImplicit(new LuaInt(45));
			expected.put(new LuaInt(30), new LuaInt(23));
			expected.put(new LuaString("x"), new LuaInt(1));
			expected.put(new LuaInt(777), LuaNil.nil);			
			assertEquals(expectedList,ret);
		}
		
		@Test
		void table0() throws Exception {
			String input = "a = {}; return a";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			assertEquals(expectedList,ret);
		}
		
		@Test
		void tablenested() throws Exception {
			String input = "a = {{1,2}}; return a[1][1]";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>(); 
			expected.add(new LuaInt(1));
			assertEquals(expected,ret);
		}
		
		
		@Test
		void tablenestedAssign() throws Exception {
			String input = "a = {{1,2}}; a[1][1] = 4;return a[1][1]";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expected = new ArrayList<>(); 
			expected.add(new LuaInt(4));
			assertEquals(expected,ret);
		}
		
		@Test
		void table1() throws Exception {
			String input = "a = {\"x\", 2, 3}; return a";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.putImplicit( new LuaString( "x" ));
			expected.putImplicit(new LuaInt(2));
			expected.putImplicit(new LuaInt(3));			
			assertEquals(expectedList,ret);
		}
		
		@Test
		void table2() throws Exception {
			String input = "a = {[\"x\"]= 2, [\"y\"]=3}; return a";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);			
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.put(new LuaString("x"), new LuaInt(2));
			expected.put(new LuaString("y"), new LuaInt(3)); 		
			assertEquals(expectedList,ret);
		}
		
		@Test
		void table3() throws Exception {
			String input = "a = {x=2, y=3}; return a";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.put(new LuaString("x"), new LuaInt(2));
			expected.put(new LuaString("y"), new LuaInt(3)); 		
			assertEquals(expectedList,ret);
		}
		
		
		@Test
		void table4() throws Exception {
			String input = "x = \"hello\" y= \"goodbye\" a = {[x]=2, [y]=3}; return a";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.put(new LuaString("hello"), new LuaInt(2));
			expected.put(new LuaString("goodbye"), new LuaInt(3)); 	
			assertEquals(expectedList,ret);
		}
		

		
		@Test
		void gotoTest0prep() throws Exception {
			String input = " x=2"
					+ "\n y=3 "
					+ "\n y=4 "
					+ "\n ::label1:: "
					+ "\n return y"
					;
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = makeExpectedWithInts(4);
			assertEquals(expected,ret);
		}
		
		@Test
		void gotoTest0() throws Exception {
			String input = " x=2"
					+ "\n y=3 goto label1 "
					+ "\n y=4 "
					+ "\n ::label1:: "
					+ "\n return y"
					;
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = makeExpectedWithInts(3);
			assertEquals(expected,ret);
		}
		
		@Test
		void gotoTest1() throws Exception{
			String input = "a=1; b=2; do a=3 goto label1 end b=3 ::label1:: a=4 return a,b";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);		
			List<LuaValue> expected = makeExpectedWithInts(4,2);
			assertEquals(expected,ret);
		}
		
		@Test
		void gotoTest2() throws Exception{
			String input = "a=0; do a=1 do a=2 goto label1  a=3 end a=4 end a=5 ::label1:: b=6 return a,b";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = makeExpectedWithInts(2,6);
			assertEquals(expected,ret);			
		}
		
		@Test
		void testBinary0() throws Exception{
			String input = "a=2+3 b=3-a c=2*4 d = c/2 e = c%3 k = e //2  u = e^4 return a,b,c,d,e,k,u";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = makeExpectedWithInts(5,-2,8,4,2,1,16);
			assertEquals(expected,ret);						
		}
		
		@Test
		void testBinaryBitWise() throws Exception{
			String input = "a=131 & 2 b = a | 3 c = b ~ 2\r\n" + 
					"d = 55 >> 2 \r\n" + 
					"e = 22 << 1 "
					+ "return a,b,c,d,e";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = makeExpectedWithInts(2,3,1,13,44);
			assertEquals(expected,ret);						
		}
		
		@Test
		void testOPStringIntObjectMix() throws Exception{
			String input = "return 0 == \"123\"";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = new ArrayList<LuaValue>();
			expected.add(new LuaBoolean(false));
			assertEquals(expected,ret);			
			
			input = "return \"345\" > \"123\"";
			show(input);
			ret = interpret(input);
			show(ret);	
			expected = new ArrayList<LuaValue>();
			expected.add(new LuaBoolean(true));
			assertEquals(expected,ret);		
			
			input = "return 4 + \"123\"";
			show(input);
			ret = interpret(input);
			show(ret);	
			expected = makeExpectedWithInts(127);
			assertEquals(expected,ret);		
			
			input = "return null and \"123\"";
			show(input);
			ret = interpret(input);
			show(ret);	
			expected = new ArrayList<LuaValue>();
			expected.add(LuaNil.nil);
			assertEquals(expected,ret);		
			
			input = "return null or \"123\"";
			show(input);
			ret = interpret(input);
			show(ret);	
			expected = new ArrayList<LuaValue>();
			expected.add(new LuaString("123"));
			assertEquals(expected,ret);		
		}
		
		@Test
		void testUnary() throws Exception{
			String input = "a = 4 a = -a b = 5 b = not 5 c = \"hello\" c = #c d = ~35"
					+ "return a,b,c,d";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expected = makeExpectedWithInts(-4,0,5,-36);
			expected.set(1, new LuaBoolean(false));
			assertEquals(expected,ret);						
		}
		
		@Test
		void testSetField0() throws Exception{
			String input = "a = 4; t={} ; t[a]=5; return t";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.put(new LuaInt(4), new LuaInt(5));
			assertEquals(expectedList,ret);
		}
		

		
		@Test 
		void testSetField1() throws Exception{
			String input = "a = {1,2,3} t= {a} dummy = print(t[1][3]) return t";
			show(input);
			List<LuaValue> ret = interpret(input);
			show(ret);	
			List<LuaValue> expectedList = new ArrayList<>();
			LuaTable a = new LuaTable();
			a.put(new LuaInt(1),new LuaInt(1));
			a.put(new LuaInt(2),new LuaInt(2));
			a.put(new LuaInt(3),new LuaInt(3));
			
			LuaTable expected = new LuaTable();
			expectedList.add(expected);
			expected.put(new LuaInt(1), a);
			assertEquals(expectedList,ret);
		}
		
		@Test
		void toNumberTest() throws Exception{
		String input = "a = toNumber(\"33\"); return a";
		show(input);
		List<LuaValue> ret = interpret(input);
		show(ret);
		LuaValue[] vals = {new LuaInt(33)};
		List<LuaValue> expected = Arrays.asList(vals);
		assertEquals(expected, ret);
		}
		
		@Test
		void testhw4() throws Exception{
		String input = "return (100+20+3) .. \" one two three\"";
		show(input);
		List<LuaValue> ret = interpret(input);
		show(ret);
		LuaValue[] vals = {new LuaString("123 one two three")};
		List<LuaValue> expected = Arrays.asList(vals);
		assertEquals(expected, ret);
		

		input = "return 123 .. \" one two three\"";
		show(input);
		ret = interpret(input);
		show(ret);
		LuaValue[] vals2 = {new LuaString("123 one two three")};
		expected = Arrays.asList(vals2);
		assertEquals(expected, ret);
		
		input = "return 123 .. 345";
		show(input);
		ret = interpret(input);
		show(ret);
		LuaValue[] vals3 = {new LuaString("123345")};
		expected = Arrays.asList(vals3);
		assertEquals(expected, ret);
		}
}
