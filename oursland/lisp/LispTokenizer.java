package oursland.lisp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.LinkedList;
import oursland.parse.BaseTokenizer;

/**
 * Parses Lisp tokens. Not complete according to Lisp spec.
 * This tokenizer is expanded as I need features.
 * @author oursland
 */
public class LispTokenizer extends BaseTokenizer {
	private LinkedList<String>	nextTokens	= new LinkedList<String>();

	public LispTokenizer(InputStream in) {
		super(in);
	}

	public LispTokenizer(String str) {
		this(new StringBufferInputStream(str));
	}

	public boolean hasNextToken() {
		try {
			return peekToken(0) != null;
		} catch(IOException e) {
			return false;
		}
	}
	
	public String peekToken(int i) throws IOException {
		while( i >= nextTokens.size() ) {
			nextTokens.addLast(parseToken());
		}
		return nextTokens.get(i);
	}

	public String nextToken() throws IOException {
		String rc; 
		if( nextTokens.size() == 0 ) {
			rc = parseToken();
		} else {
			rc = nextTokens.removeFirst();
		}
//		System.err.println(rc);
		return rc;
	}

	private String parseToken() throws IOException {
		consumeWhitespace();
		char ch = peek(0);
		switch(ch) {
			case '(':
			case ')':
				consume(ch);
				return Character.toString(ch);
			case '"':
				return parseString();
			default:
				return parseSymbol();
		}
	}

	private String parseString() throws IOException {
		StringBuffer rc = new StringBuffer();
		consume('"');
		rc.append('"');
		while(true) {
			char ch = peek(0);
			consume(ch);
			rc.append(ch);
			if( ch == '"') {
				if(rc.length() > 0) {
					return rc.toString();
				} else {
					throw new IOException("Encountered nil string token.");
				}
			}
		}
	}

	private String parseSymbol() throws IOException {
		StringBuffer rc = new StringBuffer();
		while(true) {
			int ci = peekNoEOF(0);
			char ch = (char) ci;
			if(ci == -1 || ci == '(' || ci == ')' || Character.isWhitespace(ch)) {
				if(rc.length() > 0) {
					return rc.toString();
				} else {
					throw new IOException("Encountered nil symbol token.");
				}
			} else {
				consume(ch);
				rc.append(ch);
			}
		}
	}

	private void consumeWhitespace() throws IOException {
		char ch = peek(0);
		while(Character.isWhitespace(ch)) {
			consume(ch);
			ch = peek(0);
		}
	}
	
	public static void main(String[] args) {
		String filename = "wordnet2km.txt";
		if( args.length > 0 ) {
			filename = args[0];
		}
		try {
			int count = 0;
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
			LispTokenizer t = new LispTokenizer(in);
			while(t.hasNextToken()) {
				System.out.println(t.nextToken());
				count++;
				if( count > 100 ) {
					break;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public String expect(String expect) throws IOException {
		String found = this.nextToken();
		if( !found.equals(expect) ) {
			throw new Error("Found \"" + found + "\" but expected \"" + expect + "\".");
		}
		return found;
	}

	public static double parseNumberToDouble(String number) {
		if( isFraction(number) ) {
			int sep = number.indexOf('/');
			double num = Double.parseDouble(number.substring(0, sep));
			double den = Double.parseDouble(number.substring(sep+1));
			return num/den;
		} else {
			return Double.parseDouble(number);
		}
	}

	private static boolean isFraction(String number) {
		return number.indexOf('/') > -1;
	}
}