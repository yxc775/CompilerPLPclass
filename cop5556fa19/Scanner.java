

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


import static cop5556fa19.Token.Kind.*;


import java.io.IOException;
import java.io.Reader;

public class Scanner {
	Reader r;
	boolean isEnded;
	boolean beforeisreline = false;
	
	int ch = -1;
	int curpos = -1;
	int curlines = 0;
	private enum State{
		START,
		//state below for operations
		HAVE_DASH,HAVE_EQ,HAVE_COLLON,HAVE_XOR,HAVE_DIV,HAVE_LESS,HAVE_GREA,HAVE_DOT,HAVE_DOTDOT,
		//state below for NUMLIT and INDENTIFIER and STRING
		IN_NUMLIT,IN_IDENT,IN_STRING,
		//state below to point that the comment situation
		IN_COMMENT,COMMENT_END,
		
		END;
	}


	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {	
		public LexicalException(String arg0) {
			super(arg0);
		}
	}
	
	public Scanner(Reader r) throws IOException {
		this.r = r;
		isEnded = false;
	}
	
	public void getchar() throws IOException{
		ch = r.read();
		if(!isEnded){
			curpos ++;
		}
	}
	


	public Token getNext() throws Exception {
		    //replace this code.  Just for illustration
			Token out = null;
			State state = State.START;
			StringBuilder sb = new StringBuilder();
			char returnSymbol = '\"';
			if(ch < 0) {
				getchar();
			}
			if(ch >= 128) {
				 throw new LexicalException("Illegal character read: " + ch + " at " + "line " + curlines +" " + curpos);  
			}
			
			int pos = -1;
			int line = -1;
			while(out == null) {
				switch(state) {
					case START:
						skipwhiteSpace();
						pos = this.curpos;
						line = this.curlines;
						switch(ch) {
						case '+':
							out = new Token(OP_PLUS, "+", pos, line);
							getchar();
							break;
						case '*':
							out = new Token(OP_TIMES, "*",pos, line);
							getchar();
							break;
						case '%':
							out = new Token(OP_MOD, "%", pos, line);
							getchar();
							break;
						case '^':
							out = new Token(OP_POW, "^", pos, line);
							getchar();
							break;
						case '#':
							out = new Token(OP_HASH, "#", pos, line);
							getchar();
							break;
						case '&':
							out = new Token(BIT_AMP, "&", pos, line);
							getchar();
							break;
						case '|':
							out = new Token(BIT_OR, "|", pos, line);
							getchar();
							break;
						case '~':
							state = State.HAVE_XOR;
							sb.append('~');
							getchar();
							break;
						case ',':
							out = new Token(COMMA,",",pos, line);
							getchar();
							break;
						case '(':
							out = new Token(LPAREN,"(",pos,line);
							getchar();
							break;
						case ')':
							out = new Token(RPAREN,")",pos,line);
							getchar();
							break;
						case '[':
							out = new Token(LSQUARE,"[",pos,line);
							getchar();
							break;
						case ']':
							out = new Token(RSQUARE,"]",pos,line);
							getchar();
							break;
						case '{':
							out = new Token(LCURLY,"{",pos,line);
							getchar();
							break;
						case '}':
							out = new Token(RCURLY,"}",pos,line);
							getchar();
							break;
						case ';':
							out = new Token(SEMI,";",pos,line);
							getchar();
							break;
						case ':':
						
							state = State.HAVE_COLLON;
							sb.append(':');
							getchar();
							break;
						case '=':
							
							state = State.HAVE_EQ;
							sb.append('=');
							getchar();
							break;
						case '/':
							
							state = State.HAVE_DIV;
							sb.append('/');
							getchar();
							break;
						case '<':
						
							state = State.HAVE_LESS;
							sb.append('<');
							getchar();
							break;
						case '>':
							
							state = State.HAVE_GREA;
							sb.append('>');
							getchar();
							break;
						case '.':
							state = State.HAVE_DOT;
							sb.append('.');
							getchar();
							break;
						case '-':
							state = State.HAVE_DASH;
							sb.append('-');
							getchar();
							break;
						case '\"':
							state = State.IN_STRING;
							returnSymbol = '\"';
							sb.append('\"');
							getchar();
							break;
						case '\'':
							state = State.IN_STRING;
							returnSymbol = '\'';
							sb.append('\'');
							getchar();
							break;
						case '0': 
							out = new Token(INTLIT,"0",pos,line);
							getchar();
							break;
						case -1:
							state = State.END;
							break;
						default:
							 if (Character.isDigit(ch)) {
								 state = State.IN_NUMLIT;
								 sb.append((char)ch);
								 getchar();
								 break;
								 }             
							 else if (Character.isJavaIdentifierStart(ch)) {
								 state = State.IN_IDENT;
								 sb.append((char)ch);
								 getchar();
								 break;
								 }              
							 else { 
								 throw new LexicalException("Illegal first character read: " + ch + " at " + "line " + curlines +" " + curpos);  
								 }          
							 }
						break;
					case HAVE_DASH:
						if((char)this.ch == '-') {
							state = State.IN_COMMENT;
							sb = new StringBuilder();
							getchar();
						}
						else {
							out = new Token(OP_MINUS, sb.toString(), pos, line);
						}
						break;
					case HAVE_DOT:
						if((char)this.ch == '.') {
							state = State.HAVE_DOTDOT;
							sb.append('.');
							getchar();
						}
						else {
							out = new Token(DOT, sb.toString(), pos, line);
						}
						break;
					case HAVE_DOTDOT:
						if((char)this.ch == '.') {
							sb.append('.');
							out = new Token(DOTDOTDOT, sb.toString(), pos, line);
							getchar();
						}
						else {
							out = new Token(DOTDOT, sb.toString(), pos, line);
						}
						break;
					case HAVE_LESS:
						if((char)this.ch == '=') {
							sb.append('=');
							out = new Token(REL_LE,sb.toString(),pos, line);
							getchar();
						}
						else if((char) this.ch == '<'){
							sb.append('<');
							out = new Token(BIT_SHIFTL, sb.toString(), pos, line);
							getchar();
						}
						else {
							out = new Token(REL_LT, sb.toString(), pos, line);
						}
						break;
					case HAVE_GREA:
						if((char)this.ch == '=') {
							sb.append('=');
							out = new Token(REL_GE,sb.toString(),pos, line);
							getchar();
						}
						else if((char) this.ch == '>'){
							sb.append('>');
							out = new Token(BIT_SHIFTR, sb.toString(), pos, line);
							getchar();
						}
						else {
							out = new Token(REL_GT, sb.toString(), pos, line);
						}
						break;
					case HAVE_DIV:
						if((char)this.ch == '/') {
							sb.append('/');
							out = new Token(OP_DIVDIV,sb.toString(),pos, line);
							getchar();
						}
						else {
							out = new Token(OP_DIV, sb.toString(), pos, line);
						}
						break;
					case HAVE_XOR:
						if((char)this.ch == '=') {
							sb.append('=');
							out = new Token(REL_NOTEQ,sb.toString(),pos, line);
							getchar();
						}
						else {
							out = new Token(BIT_XOR,sb.toString(),pos, line);
						}
						break;
					case HAVE_EQ:
						if((char)this.ch == '=') {
							sb.append('=');
							out = new Token(REL_EQEQ,sb.toString(),pos, line);
							getchar();
						}
						else {
							out = new Token(ASSIGN,sb.toString(),pos, line);
						}
						break;
					case HAVE_COLLON:
						if((char)this.ch == ':') {
							sb.append(':');
							out = new Token(COLONCOLON,sb.toString(),pos, line);
							getchar();
						}
						else {
							out = new Token(COLON,sb.toString(),pos, line);
						}
						break;
					case IN_STRING:
						if(ch >= 0 && ch < 128 && ((char) ch != returnSymbol) && (char)ch != '\\'){
							if((char)this.ch == '\r') {
								this.curlines ++;
								this.curpos = 0;
								beforeisreline = true;
							}
							else if((char)this.ch == '\n') {
								if(beforeisreline) {
									beforeisreline = false;
								}
								else {
									this.curlines ++;
								}
								this.curpos = 0;
							}
							else {
								beforeisreline = false;
							}
							
							if((char)ch == '\"' || (char)ch == '\'') {
								throw new LexicalException("unreturn quotation escape sequence detected " + ch);
							}
							
							sb.append((char)this.ch);
						}
						else if((char)ch == returnSymbol){
							sb.append(returnSymbol);
							out = new Token(STRINGLIT,sb.toString(),pos,line);
						}
						else if((char)ch == '\\'){
							throw new LexicalException("illegal escape sequence detected " + ch);	
						}
						else{
							throw new LexicalException("illegal ASCII character detected " + ch);
						}
						getchar();
						break;
					case IN_NUMLIT:
						if(Character.isDigit(ch)){
							sb.append((char)this.ch);
							getchar();
						}
						else {
							try {
								Integer.parseInt(sb.toString());
							}
							catch(NumberFormatException e) {
								throw new LexicalException("input int number out of the scope " + sb.toString()); 
							}
							out = new Token(INTLIT,sb.toString(),pos,line);
						}
						
						break;
					case IN_IDENT:
						if(isLegalPart()){
							sb.append((char)this.ch);
							getchar();
						}
						else {
							Token temp = checkKeyWord(sb.toString(),pos,line);
							if(temp != null) {
								out = temp;
							}
							else {
								out =  new Token(NAME,sb.toString(),pos,line);
							}
						}
						break;
					case IN_COMMENT:
						if(this.ch == '\n') {
							state = State.START;
							this.curlines ++;
							this.curpos = -1;
						}
						else if(this.ch == '\r') {
							state = State.COMMENT_END;
							this.curlines ++;
						}
						else {
						}
						getchar();
						break;
					case COMMENT_END:
						state = State.START;
						this.curpos = -1;
						if(this.ch == '\n') {
							getchar();
						}
						break;
					case END:
						this.isEnded = true;
					    out =  new Token(EOF,"eof",pos,line);
					    break;
				}
		}
			return out;
	}
	
	
	public boolean isExpectedChar() throws IOException {
		return isLegalPart() || isOP() || isLineSpacing() || isEscapeSe();
	} 
	
	
	
	
	public boolean isLegalPart() {
		return Character.isJavaIdentifierStart(ch) || Character.isDigit(ch) || (ch == '_') || (ch == '$');
	}
	
	public boolean isEscapeSe() {
		return  ch == '\b' || ch == '\f' || ch == '\n' ||
				ch == '\r' ||
				ch ==  '\t' || 
				ch == '\\' ||
				ch ==  '\"' ||
				ch == '\'' || ch == '\u0007';
	}
	
	public boolean isOP() {
		return ch ==  '+'  || ch == '-'  || ch ==  '*' || ch == '/'|| ch == '%' || ch ==  '^'|| ch == '#'
			    || ch == '&'  || ch == '~' || ch == '|' || ch == '<' ||  ch ==  '>' || ch == '=' || ch == '~'  
			     || ch == '('  || ch == ')'   ||  ch ==  '{' ||  ch == '}'  || ch == '['   || ch == ']'|| ch == ':' || ch == ';'  
			     ||  ch == ',' ||  ch ==  '.';
	}
	
	public void skipwhiteSpace() throws IOException{
		boolean reachR = false;
		while(isLineSpacing()) {
			if(isLineter()) {
				if((char)this.ch == '\n' && !reachR) {
					this.curlines ++;
				}
				else if((char)this.ch == '\r'){
					this.curlines ++;
					reachR = true;
				}
				else {
					reachR = false;
				}
				this.curpos = -1;
			}
			this.getchar();
		}
		reachR = false;
	}
	
	
	public boolean isLineSpacing() throws IOException {
		if((char)this.ch == ' ' || (char)this.ch == '\t' || (char)this.ch == '\f' || isLineter()) {	
			return true;
		}
		else {
			return false;
		} 
	}
	
	public boolean isLineter() throws IOException{
		if((char)this.ch == '\n' || (char)this.ch == '\r'){
			return true;
		}
		else {
			return false;
		}
	}
	
	public Token checkKeyWord(String x,int pos,int line) {
		Token res = null;
		switch(x) {
		case "and":
			res = new Token(KW_and,x,pos,line);
			break;
		case "break":
			res = new Token(KW_break,x,pos,line);
			break;
		case "do":
			res = new Token(KW_do,x,pos,line);
			break;
		case "else":
			res = new Token(KW_else,x,pos,line);
			break;
		case "elseif":
			res = new Token(KW_elseif,x,pos,line);
			break;
		case "end":
			res = new Token(KW_end,x,pos,line);
			break;
		case "false":
			res = new Token(KW_false,x,pos,line);
			break;
		case "for":
			res = new Token(KW_for,x,pos,line);
			break;
		case "function":
			res = new Token(KW_function,x,pos,line);
			break;
		case "goto":
			res = new Token(KW_goto,x,pos,line);
			break;
		case "if":
			res = new Token(KW_if,x,pos,line);
			break;
		case "in":
			res = new Token(KW_in,x,pos,line);
			break;
		case "local":
			res = new Token(KW_local,x,pos,line);
			break;
		case "nil":
			res = new Token(KW_nil,x,pos,line);
			break;
		case "not":
			res = new Token(KW_not,x,pos,line);
			break;
		case "or":
			res = new Token(KW_or,x,pos,line);
			break;
		case "repeat":
			res = new Token(KW_repeat,x,pos,line);
			break;
		case "return":
			res = new Token(KW_return,x,pos,line);
			break;
		case "then":
			res = new Token(KW_then,x,pos,line);
			break;
		case "true":
			res = new Token(KW_true,x,pos,line);
			break;
		case "until":
			res = new Token(KW_until,x,pos,line);
			break;
		case "while":
			res = new Token(KW_while,x,pos,line);
			break;
		default:
			break;
		}
		return res;
	}
	

	
	
}

