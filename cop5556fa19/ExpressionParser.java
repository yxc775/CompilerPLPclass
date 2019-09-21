/**
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa19.AST.Block;
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
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class ExpressionParser {
	
	@SuppressWarnings("serial")
	class SyntaxException extends Exception {
		Token t;
		
		public SyntaxException(Token t, String message) {
			super(t.line + ":" + t.pos + " " + message);
		}
	}
	
	final Scanner scanner;
	Token t;  //invariant:  this is the next token


	ExpressionParser(Scanner s) throws Exception {
		this.scanner = s;
		t = scanner.getNext(); //establish invariant
	}
	
	
	
	List<Name> namelist() throws Exception{
		List<Name> res = new ArrayList<>();
		res.add(name());
		while(isKind(COMMA)) {
			consume();
			res.add(name());
		}
		return res;
	}
	
	Name name() throws Exception{
		Token first = t;
		StringBuilder s = new StringBuilder();
		if(isKind(NAME)) {
			s.append(t.getName());
			consume();
			return new Name(first,s.toString());
		}
		else {
			error(t,"illegal format(non-name element) detected");
			return null;
		}
	}
	
	
	ParList parlist() throws Exception{
		Token first = t;
		List<Name> list = new ArrayList<>();
		boolean hasVarargs = false;
		if(isKind(NAME)) {
			list = namelist();
			if(isKind(COMMA)) {
				consume();
				match(DOTDOTDOT);
				hasVarargs = true;
			}
		}
		else if(isKind(DOTDOTDOT)) {
			hasVarargs = true;
			consume();
		}
		else {
			error(NAME,DOTDOTDOT);
		}
		return new ParList(first,list,hasVarargs);
	}
	
	FuncBody functionBody() throws Exception{
		Token first = t;
		match(LPAREN);
		ParList list = parlist();
		match(RPAREN);
		Block b = block();
		match(KW_end);
		return new FuncBody(first,list,b);
	}
	
	
	
 	Exp exp() throws Exception {
		Token first = t;
		Exp e0 = andExp();
		while (isKind(KW_or)) {
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
 	
 	private Exp andExp() throws Exception{
 		Token first = t;
		Exp e0 = compareExp();
		while(isKind(KW_and)) {
			Token op = consume();
			Exp e1 = compareExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		
		return e0;
	}

 	
 	
 	private Exp compareExp() throws Exception{
 		Token first = t;
		Exp e0 = bitorExp();
		while(isKind(REL_LT) || isKind(REL_GT) || isKind(REL_LE) || isKind(REL_GE) || isKind(REL_NOTEQ) || isKind(REL_EQEQ)) {
			Token op = consume();
			Exp e1 = bitorExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		
		return e0;
 	}
 	
 	private Exp bitorExp() throws Exception{
 		Token first = t;
		Exp e0 = bitxorExp();
		while(isKind(BIT_OR)) {
			Token op = consume();
			Exp e1 = bitxorExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0;
 	}
 	
 	private Exp bitxorExp() throws Exception{
 		Token first = t;
		Exp e0 = bitampExp();
		while(isKind(BIT_XOR)) {
			Token op = consume();
			Exp e1 = bitampExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0;
 	}
 	
 	private Exp bitampExp() throws Exception{
 		Token first = t;
		Exp e0 = bitshiftExp();
		while(isKind(BIT_AMP)) {
			Token op = consume();
			Exp e1 = bitshiftExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0;
 	}
 	
 	public Exp bitshiftExp() throws Exception{
 		Token first = t;
		Exp e0 = dotdotExp();
		while(isKind(BIT_SHIFTL) || isKind(BIT_SHIFTR)) {
			Token op = consume();
			Exp e1 = dotdotExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0; 		
 	}
 	
 	public Exp dotdotExp() throws Exception{
 		Token first = t;
		Exp e0 = plusminusExp();
		while(isKind(DOTDOT)) {
			Token op = consume();
			Exp e1 = dotdotExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0; 		
 	}
 	
 	public Exp plusminusExp() throws Exception{
 		Token first = t;
		Exp e0 = factorExp();
		while(isKind(OP_PLUS) || isKind(OP_MINUS)) {
			Token op = consume();
			Exp e1 = factorExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0; 		
 	}
 	
 	public Exp factorExp() throws Exception{
 		Token first = t;
		Exp e0 = unaExp();
		while(isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_DIVDIV) || isKind(OP_MOD)) {
			Token op = consume();
			Exp e1 = unaExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0; 	
 	}
 	
 	public Exp unaExp() throws Exception{
 		Token first = t;
 		Exp e0 = null;
		if(isKind(KW_not) || isKind(OP_HASH) || isKind(OP_MINUS) || isKind(BIT_XOR)) {
			Token op = consume();
			Exp e1 = powExp();
			e0 = new ExpUnary(first,op.kind,e1);
			return e0;
		}
		else {
			e0 = powExp();
			return e0;
		}
 	}
 	
 	public Exp powExp() throws Exception{
 		Token first = t;
		Exp e0 = atomExp();
		while(isKind(OP_POW)) {
			Token op = consume();
			Exp e1 = powExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0; 	
 	}
 	
 	public Exp atomExp() throws Exception{
 		Token first = t;
 		Exp e0 = null;
 		switch(first.kind) {
 		case KW_nil:
 			consume();
 			e0 = new ExpNil(first);
 			break;
 		case KW_false:
 			consume();
 			e0 = new ExpFalse(first);
 			break;
 		case KW_true:
 			consume();
 			e0 = new ExpTrue(first);
 			break;
 		case INTLIT:
 			consume();
 			e0 = new ExpInt(first);
 			break;
 		case STRINGLIT:
 			consume();
 			e0 = new ExpString(first);
 			break;
 		case NAME:
 			consume();
 			e0 = new ExpName(first);
 			break;
 		case LPAREN:
 			match(LPAREN);
 			e0 = exp();
 			match(RPAREN);
 			break;
 		case DOTDOTDOT:
 			consume();
 			e0 = new ExpVarArgs(first);
 			break;
 		case KW_function:
 			FuncBody e1 = functionBody();
 			break;
 		default:
 			break;
 		}
 		if(e0 != null) {
 			return e0;
 		}
 		else {
 			error(t,"illegal terminal statement");
 			return null;
 		}
 	}
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	


	private Block block() {
		return new Block(null);  //this is OK for Assignment 2
	}


	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind kind) throws Exception {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		error(kind);
		return null; // unreachable
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind... kinds) throws Exception {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		error(kinds);
		return null; // unreachable
	}

	Token consume() throws Exception {
		Token tmp = t;
        t = scanner.getNext();
		return tmp;
	}
	
	void error(Kind... expectedKinds) throws SyntaxException {
		String kinds = Arrays.toString(expectedKinds);
		String message;
		if (expectedKinds.length == 1) {
			message = "Expected " + kinds + " at " + t.line + ":" + t.pos;
		} else {
			message = "Expected one of" + kinds + " at " + t.line + ":" + t.pos;
		}
		throw new SyntaxException(t, message);
	}

	void error(Token t, String m) throws SyntaxException {
		String message = m + " at " + t.line + ":" + t.pos;
		throw new SyntaxException(t, message);
	}
	


}
