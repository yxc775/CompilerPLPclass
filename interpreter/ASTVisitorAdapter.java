package interpreter;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import cop5556fa19.Token;
import cop5556fa19.Token.Kind;
import cop5556fa19.AST.ASTVisitor;
import cop5556fa19.AST.Block;
import cop5556fa19.AST.Chunk;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpList;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldList;
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
import cop5556fa19.AST.StatGoto;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatLocalAssign;
import cop5556fa19.AST.StatLocalFunc;
import cop5556fa19.AST.StatRepeat;
import cop5556fa19.AST.StatWhile;

public abstract class ASTVisitorAdapter implements ASTVisitor {
	
	@SuppressWarnings("serial")
	public static class StaticSemanticException extends Exception{
		
			public StaticSemanticException(Token first, String msg) {
				super(first.line + ":" + first.pos + " " + msg);
			}
		}
	
	
	@SuppressWarnings("serial")
	public
	static class TypeException extends Exception{

		public TypeException(String msg) {
			super(msg);
		}
		
		public TypeException(Token first, String msg) {
			super(first.line + ":" + first.pos + " " + msg);
		}
		
	}
	
	public abstract List<LuaValue> load(Reader r) throws Exception;

	@Override
	public Object visitExpNil(ExpNil expNil, Object arg) {
		return LuaNil.nil;
	}

	@Override
	public Object visitExpBin(ExpBinary expBin, Object arg) throws Exception {
		Token.Kind operation = expBin.op;
		LuaValue e1exp = (LuaValue) expBin.e0.visit(this, arg);
		LuaValue e2exp = (LuaValue) expBin.e1.visit(this, arg);
		if(isArithOP(operation)) {
			LuaInt e1int = null;
			LuaInt e2int = null;
			if(e1exp instanceof LuaInt && e2exp instanceof LuaInt) {
				e1int = (LuaInt)e1exp;
				e2int = (LuaInt)e2exp;
			}
			else {
				throw new TypeException(expBin.firstToken,"illegal arithmetic operation used on non-int val");
			}
		    switch(operation) {
			case OP_PLUS:
				return new LuaInt(e1int.v + e2int.v);
			case OP_MINUS:
				return new LuaInt(e1int.v - e2int.v);
			case OP_TIMES:
				return new LuaInt(e1int.v * e2int.v);
			case OP_DIV:
				if(e2int.v == 0) {
					throw new TypeException(expBin.firstToken,"divide by 0!");
				}
				return new LuaInt(e1int.v / e2int.v);
			case OP_DIVDIV:
				if(e2int.v == 0) {
					throw new TypeException(expBin.firstToken,"divide divide by 0!");
				}
				return new LuaInt((e1int.v / e2int.v)/e2int.v);
			case OP_POW:
				return new LuaInt((int)Math.pow((double)e1int.v, (double)e2int.v));
			case OP_MOD:
				if(e2int.v == 0) {
					throw new TypeException(expBin.firstToken,"mod by 0!");
				}
				return new LuaInt(e1int.v % e2int.v);
			case BIT_AMP:
				return new LuaInt(e1int.v & e2int.v);
			case BIT_XOR:
				return new LuaInt(e1int.v ^ e2int.v);
			case BIT_OR:
				return new LuaInt(e1int.v | e2int.v);
			case BIT_SHIFTL:
				return new LuaInt(e1int.v << e2int.v);
			case BIT_SHIFTR:
				return new LuaInt(e1int.v >> e2int.v);
			default:
				throw new TypeException(expBin.firstToken,"unknown arith operator!");
			}
		}
		else if(isRelational(operation)) {
			if((e1exp instanceof LuaInt && e1exp instanceof LuaString) || (e2exp instanceof LuaInt && e1exp instanceof LuaString) ) {
				if(operation == Token.Kind.REL_EQEQ) {
					return new LuaBoolean(false);
				}
				else if(operation == Token.Kind.REL_NOTEQ) {
					return new LuaBoolean(true);
				}
				else {
					throw new TypeException(expBin.firstToken,"illegal Int/String operation!");
				}
			}
			
			if(e1exp instanceof LuaString && e2exp instanceof LuaString) {
				LuaString e1str = (LuaString)e1exp;
				LuaString e2str = (LuaString)e2exp;
				
				if(operation == Token.Kind.REL_EQEQ) {
					return new LuaBoolean(e1str.value.equals(e2str.value));
				}
				else if(operation == Token.Kind.REL_NOTEQ) {
					return new LuaBoolean(!e1str.value.equals(e2str.value));
				}
				else {
					throw new TypeException(expBin.firstToken,"illegal String operation!");
				}
			}
			
			LuaInt e1int = null;
			LuaInt e2int = null;
			if(e1exp instanceof LuaInt && e2exp instanceof LuaInt) {
				e1int = (LuaInt)e1exp;
				e2int = (LuaInt)e2exp;
			}
			else {
				throw new TypeException(expBin.firstToken,"non int object used with relational operator!");
			}
			
			switch(operation) {
				case REL_LT:
					return new LuaBoolean(e1int.v < e2int.v);
				case REL_LE:
					return new LuaBoolean(e1int.v <= e2int.v);
				case REL_GT:
					return new LuaBoolean(e1int.v > e2int.v);
				case REL_GE:
					return new LuaBoolean(e1int.v >= e2int.v);
				case REL_EQEQ:
					return new LuaBoolean(e1int.v == e2int.v);
				case REL_NOTEQ:
					return new LuaBoolean(e1int.v != e2int.v);
				default:
					throw new TypeException(expBin.firstToken,"unknown relation operator!");
			}	
		}
		else if(isLogical(operation)) {
			switch(operation) {
			case KW_and:
				if(e1exp instanceof LuaBoolean) {
					if(!((LuaBoolean)e1exp).value) {
						return e1exp;
					}
					else {
						return e2exp;
					}
				}
				else if(e1exp instanceof LuaNil) {
					return e1exp;
				}
				else {
					return e2exp;
				}
			case KW_or:
				if(e1exp instanceof LuaBoolean) {
					if(!((LuaBoolean)e1exp).value) {
						return e2exp;
					}
					else {
						return e1exp;
					}
				}
				else if(e1exp instanceof LuaNil) {
					return e2exp;
				}
				else {
					return e1exp;
				}
			default:
				throw new TypeException(expBin.firstToken,"unknown logic operator!");
		}	
		}
		else if(operation == Token.Kind.DOTDOT ) {
			if(e1exp instanceof LuaString && e2exp instanceof LuaString) {
				return new LuaString(((LuaString)e1exp).value + ((LuaString)e2exp).value);
			}
			else {
				throw new TypeException(expBin.firstToken,"illegal string concatenation!");
			}
		}
		else {
			throw new TypeException(expBin.firstToken,"illegal binary operation detected");
		}
	}
	
	public boolean isArithOP(Token.Kind element) {
		return element == Token.Kind.OP_PLUS || element == Token.Kind.OP_MINUS || 
				element == Token.Kind.OP_TIMES || element == Token.Kind.OP_DIV ||
				element == Token.Kind.OP_DIVDIV || element == Token.Kind.OP_POW ||
				element == Token.Kind.OP_MOD || element == Token.Kind.BIT_AMP ||
				element == Token.Kind.BIT_XOR || element == Token.Kind.BIT_OR ||
				element == Token.Kind.BIT_SHIFTL || element == Token.Kind.BIT_SHIFTR;
	}
	
	public boolean isRelational(Token.Kind element) {
		return element == Token.Kind.REL_LT || element == Token.Kind.REL_LE || 
				element == Token.Kind.REL_GT || element == Token.Kind.REL_GE ||
				element == Token.Kind.REL_EQEQ || element == Token.Kind.REL_NOTEQ;
	}
	
	public boolean isLogical(Token.Kind element) {
		return element == Token.Kind.KW_and || element == Token.Kind.KW_or;
	}
	
	

	@Override
	public Object visitUnExp(ExpUnary unExp, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpInt(ExpInt expInt, Object arg) {
		return new LuaInt(expInt.v);
	}

	@Override
	public Object visitExpString(ExpString expString, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpTable(ExpTable expTableConstr, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpList(ExpList expList, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitParList(ParList parList, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFunDef(ExpFunction funcDec, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitName(Name name, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		List<LuaValue> res = new LinkedList<>();
		for(Stat statement: block.stats) {
			List<LuaValue> item = (List<LuaValue>)statement.visit(this, arg);
			if(!item.isEmpty()) {
				res.addAll(item);
			}
		}
		return res;
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg, Object arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatGoto(StatGoto statGoto, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatDo(StatDo statDo, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatWhile(StatWhile statWhile, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatRepeat(StatRepeat statRepeat, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatIf(StatIf statIf, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatFor(StatFor statFor1, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatForEach(StatForEach statForEach, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFuncName(FuncName funcName, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatFunction(StatFunction statFunction, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatLocalFunc(StatLocalFunc statLocalFunc, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatLocalAssign(StatLocalAssign statLocalAssign, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitRetStat(RetStat retStat, Object arg) throws Exception {
		List<LuaValue> retvalues = new LinkedList<>();
		for(Exp expression: retStat.el) {
			retvalues.add((LuaValue)expression.visit(this, arg));
		}
		return retvalues;
	}

	@Override
	public Object visitChunk(Chunk chunk, Object arg) throws Exception {
		return chunk.block.visit(this, arg);
	}

	@Override
	public Object visitFieldExpKey(FieldExpKey fieldExpKey, Object object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFieldNameKey(FieldNameKey fieldNameKey, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object visitFieldImplicitKey(FieldImplicitKey fieldImplicitKey, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpTrue(ExpTrue expTrue, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpFalse(ExpFalse expFalse, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFuncBody(FuncBody funcBody, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpVarArgs(ExpVarArgs expVarArgs, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatAssign(StatAssign statAssign, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpTableLookup(ExpTableLookup expTableLookup, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpFunctionCall(ExpFunctionCall expFunctionCall, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLabel(StatLabel statLabel, Object ar) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFieldList(FieldList fieldList, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpName(ExpName expName, Object arg) {
		throw new UnsupportedOperationException();
	}



}
