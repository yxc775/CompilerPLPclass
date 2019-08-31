

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
			while(out == null) {
				switch(state) {
					case START:
						skipwhiteSpace();
						/*if(this.ch == - 1){
					    	state = State.END;
					    }
					    else {
					    	throw new LexicalException("Useful error message");
					    }*/
						switch(ch) {
						case ',':
							out = new Token(COMMA,",",curpos,curlines);
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
							out = new Token(REL_EQEQ,sb.toString(),curpos,curlines);
						}
						else {
							out = new Token(ASSIGN,sb.toString(),curpos - 1,curlines);
						}
						break;
					case HAVE_COLLON:
						if((char)this.ch == ':') {
							sb.append(':');
							out = new Token(COLONCOLON,sb.toString(),curpos,curlines);
							getchar();
						}
						else {
							out = new Token(COLON,sb.toString(),curpos - 1,curlines);
							state = State.START;
						}
						break;
					case IN_NUMLIT:
						break;
					case IN_INDEN:
						break;
					case END:
						this.isEnded = true;
					    out =  new Token(EOF,"eof",curpos,curlines);
					    break;
					default:
						throw new LexicalException("Useful error message");
				}
			}
			return out;
		}
	
	public void skipwhiteSpace() throws IOException{
		while((char)this.ch == ' ') {
			this.getchar();
		}
	}

	
	
}

