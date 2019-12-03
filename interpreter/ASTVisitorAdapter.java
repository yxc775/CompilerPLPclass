package interpreter;

import java.io.Reader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cop5556fa19.Token;
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
import cop5556fa19.AST.Field;
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
	boolean isbreaking = false;
	boolean whileRunning = false;
	boolean repeationg = true;
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
			if(e1exp instanceof LuaInt) {
				e1int = (LuaInt)e1exp;
			}
			else if(e1exp instanceof LuaString){
				e1int = new LuaInt(Integer.parseInt(((LuaString)e1exp).value));
			}
			else {
				throw new TypeException(expBin.firstToken,"illegal arithmetic operation used on non-int val");
			}
			
			if(e2exp instanceof LuaInt) {
				e2int = (LuaInt)e2exp;
			}
			else if(e2exp instanceof LuaString){
				e2int = new LuaInt(Integer.parseInt(((LuaString)e2exp).value));
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
					throw new TypeException(expBin.firstToken,"floor divide by 0!");
				}
				return new LuaInt(Math.floorDiv(e1int.v, e2int.v));
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
			if((e1exp instanceof LuaInt && e2exp instanceof LuaString) || (e2exp instanceof LuaInt && e1exp instanceof LuaString)) {
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
			else if(e1exp instanceof LuaString && e2exp instanceof LuaString) {
				LuaString e1str = (LuaString)e1exp;
				LuaString e2str = (LuaString)e2exp;
				Collator locale = Collator.getInstance();
				switch(operation) {
				case REL_LT:
					return new LuaBoolean(locale.compare(e1str.value, e2str.value) < 0);
				case REL_LE:
					return new LuaBoolean(locale.compare(e1str.value, e2str.value) <= 0);
				case REL_GT:
					return new LuaBoolean(locale.compare(e1str.value, e2str.value) > 0);
				case REL_GE:
					return new LuaBoolean(locale.compare(e1str.value, e2str.value) >= 0);
				case REL_EQEQ:
					return new LuaBoolean(e1str.value.equals(e2str.value));
				case REL_NOTEQ:
					return new LuaBoolean(!e1str.value.equals(e2str.value));
				default:
					throw new TypeException(expBin.firstToken,"illegal String operation!");
				}	
			}
			else if(e1exp instanceof LuaInt && e2exp instanceof LuaInt) {
				LuaInt e1int = (LuaInt)e1exp;
				LuaInt e2int = (LuaInt)e2exp;
				
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
			else {
				throw new TypeException(expBin.firstToken,"invalid operands on relation operators!");
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
			LuaString e1str = null;
			LuaString e2str = null;
			
			
			
			if(e1exp instanceof LuaInt) {
				e1str = new LuaString(((LuaInt)e1exp).toString());
			}
			else if(e1exp instanceof LuaString){
				e1str = (LuaString)e1exp;
			}
			else {
				throw new TypeException(expBin.firstToken,"illegal arithmetic operation used on non-int val");
			}
			
			if(e2exp instanceof LuaInt) {
				e2str =  new LuaString(((LuaInt)e2exp).toString());;
			}
			else if(e2exp instanceof LuaString){
				e2str = (LuaString)e2exp;
			}
			else {
				throw new TypeException(expBin.firstToken,"illegal arithmetic operation used on non-int val");
			}
			
			return new LuaString(e1str.value + e2str.value);
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
		Token.Kind pre = unExp.op;
		LuaValue val = (LuaValue)unExp.e.visit(this, arg);
		switch(pre) {
		case OP_MINUS:
			if(val instanceof LuaInt) {
				return new LuaInt(-((LuaInt)val).v);
			}
			else {
				throw new  TypeException(unExp.firstToken,"cannot apply - on non-int val");
			}
		case KW_not:
			if(val instanceof LuaBoolean) {
				return new LuaBoolean(!((LuaBoolean)val).value);
			}
			else if(val instanceof LuaNil) {
				return new LuaBoolean(true);
			}
			else {
				return new LuaBoolean(false);
			}
		case OP_HASH:
			if(val instanceof LuaString) {
				return new LuaInt(((LuaString)val).value.length());
			}
			else {
				throw new  TypeException(unExp.firstToken,"cannot apply # on non-String val");
			}
		case BIT_XOR:
			if(val instanceof LuaInt) {
				return new LuaInt(~((LuaInt)val).v);
			}
			else {
				throw new  TypeException(unExp.firstToken,"cannot apply ~ on non-int val");
			}
		default:
			throw new  TypeException(unExp.firstToken,"unknown unary operator");
		}		
	}

	@Override
	public Object visitExpInt(ExpInt expInt, Object arg) {
		return new LuaInt(expInt.v);
	}

	@Override
	public Object visitExpString(ExpString expString, Object arg) {
		return new LuaString(expString.v);
	}

	@Override
	public Object visitExpTable(ExpTable expTableConstr, Object arg) throws Exception {
		LuaTable newtable = new LuaTable();
		for(Field element: expTableConstr.fields) {
			if(element instanceof FieldImplicitKey) {
				newtable.putImplicit((LuaValue)element.visit(this, arg));
			}
			else if(element instanceof FieldNameKey) {
				Object[] ans = (Object[])element.visit(this,arg);
				newtable.put((String)ans[0],(LuaValue)ans[1]);
			}
			else if(element instanceof FieldExpKey) {
				Object[] ans = (Object[])element.visit(this,arg);
				newtable.put((LuaValue)ans[0],(LuaValue)ans[1]);
			}
			else {
				throw new TypeException(expTableConstr.firstToken,"illegal table construction detected");
			}
		}
		return newtable;
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

	@SuppressWarnings("unchecked")
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		List<LuaValue> res = new LinkedList<>();
		int targetGoto = block.stats.size() - 1;
		for(int i = 0; i <= targetGoto; i ++) {
				Stat statement = block.stats.get(i);
				Object item = statement.visit(this, arg);
				if(isbreaking) {
					if(!whileRunning) {
						isbreaking = false;
					}
					break;
				}
				
				if(item instanceof List<?>) {
					res = (List<LuaValue>)item;
					break;
				}
				
				if(statement instanceof StatGoto) {
					break;
				}
		}
		

		if(res.size()>=1) {
			return res;
		}
		else {
			return null;
		}
	}
	
	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg, Object arg2) {
		return arg;
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg) throws Exception {
		this.isbreaking = true;
		return LuaNil.nil;
	}

	@Override
	public Object visitStatGoto(StatGoto statGoto, Object arg) throws Exception {
		Block jumpto = statGoto.label.enclosingBlock;
		int position = statGoto.label.index + 1;
		List<Stat> statlist = new ArrayList<>();
		for(int i = position; i < jumpto.stats.size(); i ++) {
			statlist.add(jumpto.stats.get(i));
		}
		
		Block destination = new Block(jumpto.firstToken,statlist);
		return destination.visit(this, arg);
	}

	@Override
	public Object visitStatDo(StatDo statDo, Object arg) throws Exception {
		return statDo.b.visit(this, arg);
	}

	@Override
	public Object visitStatWhile(StatWhile statWhile, Object arg) throws Exception {
		Exp condition = statWhile.e;
		Block statement = statWhile.b;
		Object state = condition.visit(this, arg);
		if(isbreaking) {
			isbreaking = false;
			whileRunning = false;
			return LuaNil.nil;
		}
		
		if(!((state instanceof LuaBoolean && !((LuaBoolean)state).value) || (state instanceof LuaNil))) {
			whileRunning = true;
			Object retornot = statement.visit(this, arg);
			if(!(retornot instanceof List<?>)) {
				return visitStatWhile(statWhile,arg);
			}
			else {
				whileRunning = false;
				return retornot;
			}
		}
		else {
			whileRunning = false;
			return LuaNil.nil;
		}
	}

	@Override
	public Object visitStatRepeat(StatRepeat statRepeat, Object arg) throws Exception {
		Exp condition = statRepeat.e;
		Block statement = statRepeat.b;
		whileRunning = true;
		Object toreturn = statement.visit(this, arg);
		if(isbreaking) {
			isbreaking = false;
			whileRunning = false;
			return LuaNil.nil;
		}
		Object  state = condition.visit(this, arg);
		
		if(!((state instanceof LuaBoolean && !((LuaBoolean)state).value) || (state instanceof LuaNil))) {
			whileRunning = false;
			return LuaNil.nil;
		}
		else {
			if(!(toreturn instanceof List<?>)) {
				return visitStatRepeat(statRepeat,arg);
			}
			else {
				whileRunning = false;
				return toreturn;
			}
		}
	}

	@Override
	public Object visitStatIf(StatIf statIf, Object arg) throws Exception {
		int chose = -1;
		for(int i = 0; i < statIf.es.size(); i ++) {
			Exp exp = statIf.es.get(i);
			LuaValue item = (LuaValue)exp.visit(this, arg);
			
			if(!((item instanceof LuaBoolean && !((LuaBoolean)item).value) || (item instanceof LuaNil))) {
				chose = i;
				return statIf.bs.get(i).visit(this, arg);
			}
		}
		
		return  LuaNil.nil;
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
		
		Object[] ans = new Object[2];
		ans[0] = fieldExpKey.key.visit(this, object);
		ans[1] = fieldExpKey.value.visit(this, object);
		return ans;
	}

	@Override
	public Object visitFieldNameKey(FieldNameKey fieldNameKey, Object arg) throws Exception {
		Object[] ans = new Object[2];
		ans[0] = fieldNameKey.name.name;
		ans[1] = fieldNameKey.exp.visit(this, arg);
		return ans;
	}
	
	@Override
	public Object visitFieldImplicitKey(FieldImplicitKey fieldImplicitKey, Object arg) throws Exception {
		return fieldImplicitKey.exp.visit(this, arg);
	}

	@Override
	public Object visitExpTrue(ExpTrue expTrue, Object arg) {
		return new LuaBoolean(true);
	}

	@Override
	public Object visitExpFalse(ExpFalse expFalse, Object arg) {
		return new LuaBoolean(false);
	}

	@Override
	public Object visitFuncBody(FuncBody funcBody, Object arg) throws Exception {
		System.out.println("funcbo miss");
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpVarArgs(ExpVarArgs expVarArgs, Object arg) {
		System.out.println("EVA miss");
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatAssign(StatAssign statAssign, Object arg) throws Exception {
		LuaTable globalen = (LuaTable)arg;
		List<LuaValue> assigned = new LinkedList<>();
		
		for(Exp val: statAssign.expList) {
			assigned.add(((LuaValue)val.visit(this, arg)));
		}
		
		for(int i = 0; i < statAssign.varList.size();i++) {
			LuaValue key = null;
			LuaValue variable = LuaNil.nil;
			if(i < assigned.size()) {
				variable = assigned.get(i);
			}
			if(statAssign.varList.get(i) instanceof ExpName) {
				key = new LuaString(((ExpName)statAssign.varList.get(i)).name);
			}
			else if(statAssign.varList.get(i) instanceof ExpTableLookup) {
				LuaValue tk = (LuaValue)((ExpTableLookup)statAssign.varList.get(i)).key.visit(this, arg);
				((LuaTable)((ExpTableLookup)statAssign.varList.get(i)).table.visit(this, arg)).put(tk, variable);
				continue;
			}
			else {
				throw new TypeException(statAssign.firstToken,"unknown assigned Expression operator!");
			}
					
			globalen.put(key, variable);
		}
		
		return LuaNil.nil;
	}

	@Override
	public Object visitExpTableLookup(ExpTableLookup expTableLookup, Object arg) throws Exception {
		LuaTable table = (LuaTable)expTableLookup.table.visit(this, arg);
		LuaValue key = (LuaValue)expTableLookup.key.visit(this, arg);

		if(table instanceof LuaTable) {
			return table.get(key);
		}
		else {
			throw new TypeException(expTableLookup.firstToken,"non-existed table detected");
		}
	}

	@Override
	public Object visitExpFunctionCall(ExpFunctionCall expFunctionCall, Object arg) throws Exception {
		JavaFunction todo = (JavaFunction)(((LuaTable)arg).get(((ExpName)expFunctionCall.f).name));
		List<LuaValue> input = new ArrayList<>();
		for(Exp expr: expFunctionCall.args) {
			input.add( (LuaValue)expr.visit(this, arg));
		}
		
		Object item = todo.call(input);
		if(item instanceof List<?> && ((List) item).size() == 1) {
			return ((List<LuaValue>)item).get(0);
		}
		else {
			return LuaNil.nil;
		}
	}

	@Override
	public Object visitLabel(StatLabel statLabel, Object arg) {
		return LuaNil.nil;
	}

	@Override
	public Object visitFieldList(FieldList fieldList, Object arg) {
		System.out.println("fieldlist miss");
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpName(ExpName expName, Object arg) {
		LuaValue toret = ((LuaTable)arg).get(expName.name);
		if(toret != null) {
			return toret;
		}
		else {
			return LuaNil.nil;
		}
	}



}
