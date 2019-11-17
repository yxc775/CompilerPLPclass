package interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cop5556fa19.AST.ASTVisitor;
import cop5556fa19.AST.Block;
import cop5556fa19.AST.Chunk;
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

public class StaticAnalysis implements ASTVisitor{
	public Stack<Map<String,StatLabel>> scope = new Stack<>();
	@Override
	public Object visitExpNil(ExpNil expNil, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return LuaNil.nil;
	}

	@Override
	public Object visitExpBin(ExpBinary expBin, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitUnExp(ExpUnary unExp, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpInt(ExpInt expInt, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpString(ExpString expString, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpTable(ExpTable expTableConstr, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpList(ExpList expList, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitParList(ParList parList, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitFunDef(ExpFunction funcDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitName(Name name, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Map<String,StatLabel> maplabel = new HashMap<>();
		scope.push(maplabel);
		for(int i = 0; i < block.stats.size(); i ++) {
			Stat cur = block.stats.get(i);
			if(cur instanceof StatLabel) {
				((StatLabel) cur).enclosingBlock = block;
				((StatLabel) cur).setIndex(i);
				if(!maplabel.containsKey(((StatLabel) cur).label.name)) {
					maplabel.put(((StatLabel) cur).label.name, ((StatLabel) cur));
				}
				else {
					throw new StaticSemanticException(cur.firstToken,"repeated declaration of Label in the same scope!");
				}
			}
		}
		
		
		for(int i = 0; i < block.stats.size(); i ++) {
			Stat cur = block.stats.get(i);
			if(!(cur instanceof StatLabel)) {
				cur.visit(this, arg);
			}
		}
		scope.pop();		
		return arg;
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg, Object arg2) {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatGoto(StatGoto statGoto, Object arg) throws Exception {
		boolean exist = false;
		for(int i = scope.size() - 1; i >= 0; i --) {
			if(scope.get(i).containsKey(statGoto.name.name)) {
				exist = true;
				
				statGoto.label = scope.get(i).get(statGoto.name.name);
				break;
			}
		}
		if(exist) {
			return arg;
		}
		else {
			throw new StaticSemanticException(statGoto.firstToken,"targeting non-existed label!");
		}
	}

	@Override
	public Object visitStatDo(StatDo statDo, Object arg) throws Exception {
		statDo.b.visit(this, arg);
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatWhile(StatWhile statWhile, Object arg) throws Exception {
		statWhile.b.visit(this, arg);
		return arg;
	}

	@Override
	public Object visitStatRepeat(StatRepeat statRepeat, Object arg) throws Exception {
		statRepeat.b.visit(this, arg);
		return arg;
	}

	@Override
	public Object visitStatIf(StatIf statIf, Object arg) throws Exception {
		for(Block b: statIf.bs) {
			b.visit(this, arg);
		}
		return arg;
	}

	@Override
	public Object visitStatFor(StatFor statFor1, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatForEach(StatForEach statForEach, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitFuncName(FuncName funcName, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatFunction(StatFunction statFunction, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatLocalFunc(StatLocalFunc statLocalFunc, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatLocalAssign(StatLocalAssign statLocalAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitRetStat(RetStat retStat, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitChunk(Chunk chunk, Object arg) throws Exception {
		visitBlock(chunk.block,arg);
		return arg;
	}

	@Override
	public Object visitFieldExpKey(FieldExpKey fieldExpKey, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitFieldNameKey(FieldNameKey fieldNameKey, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitFieldImplicitKey(FieldImplicitKey fieldImplicitKey, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpTrue(ExpTrue expTrue, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpFalse(ExpFalse expFalse, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitFuncBody(FuncBody funcBody, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpVarArgs(ExpVarArgs expVarArgs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitStatAssign(StatAssign statAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpTableLookup(ExpTableLookup expTableLookup, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpFunctionCall(ExpFunctionCall expFunctionCall, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitLabel(StatLabel statLabel, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitFieldList(FieldList fieldList, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

	@Override
	public Object visitExpName(ExpName expName, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return arg;
	}

}
