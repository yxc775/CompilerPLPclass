

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
	int ch = -1;
	int curpos = -1;
	int curlines = 0;
	private enum State{
		START,HAVE_EQ,HAVE_COLLON, IN_NUMLIT,IN_INDEN,END;
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
			if(ch < 0) {
				getchar();
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
						case '-':
							out = new Token(OP_MINUS, "-", pos, line);
							getchar();
							break;
						case '*':
							out = new Token(OP_TIMES, "*",pos, line);
							getchar();
							break;
						case '/':
							out = new Token(OP_DIV, "/", pos, line);
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
							
							break;
						case ',':
							out = new Token(COMMA,",",pos, line);
							getchar();
							break;
						case ':':
							sb = new StringBuilder();
							state = State.HAVE_COLLON;
							sb.append(':');
							getchar();
							break;
						case '=':
							sb = new StringBuilder();
							state = State.HAVE_EQ;
							sb.append('=');
							getchar();
							break;
						case -1:
							state = State.END;
							break;
						default:
							throw new LexicalException("Useful error message");
						}
					    break;
					case HAVE_EQ:
						if((char)this.ch == '=') {
							sb.append('=');
							out = new Token(REL_EQEQ,sb.toString(),pos, line);
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
							state = State.START;
						}
						break;
					case IN_NUMLIT:
						break;
					case IN_INDEN:
						break;
					case END:
						this.isEnded = true;
					    out =  new Token(EOF,"eof",pos,line);
					    break;
					default:
						throw new LexicalException("Useful error message");
				}
			}
			return out;
		}
	
	public void skipwhiteSpace() throws IOException{
		boolean reachR = false;
		while((char)this.ch == ' ' || (char)this.ch == '\t' || (char)this.ch == '\f' || isLineter()) {
			
			if(isLineter()) {
				if((char)this.ch == '\n' && !reachR) {
					this.curlines ++;
					this.curpos = 0;
				}
				else if((char)this.ch == '\r'){
					this.curlines ++;
					this.curpos = 0;
					reachR = true;
				}
				else {
					reachR = false;
				}
			}
			this.getchar();
			
		}
		reachR = false;
	}
	
	public boolean isLineter() throws IOException{
		if((char)this.ch == '\n' || (char)this.ch == '\r'){
			return true;
		}
		else {
			return false;
		}
	}

	
	
}

