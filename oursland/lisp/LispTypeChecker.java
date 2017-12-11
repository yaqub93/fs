package oursland.lisp;

import jacol.LispInterpreter;

/**
 * @author oursland
 */
public class LispTypeChecker {
	public static LispTypeChecker	instance;
	private final LispInterpreter lisp;
	
	public LispTypeChecker(LispInterpreter lisp) {
		this.lisp = lisp;
	}
	
	public boolean isNumber(String s) {
		int i = 0;
		while(i < s.length()) {
			char c = s.charAt(i++);
			if( !Character.isDigit(c) ) {
				break;
			}
		}
		if( i < s.length() ) {
			char c = s.charAt(i++);
			if( c == '.') {
			} else if( c == '/' ) {
				if( i < s.length() ) {
					if( !Character.isDigit(s.charAt(i++))) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		while(i < s.length()) {
			char c = s.charAt(i++);
			if( !Character.isDigit(c) ) {
				return false;
			}
		}
		return true;
//		String cmd = "(numberp '" + s + ")";
//		String result = lisp.eval(cmd);
//		return result.equalsIgnoreCase("T");
	}
	
	public boolean isSymbol(String s) {
		String cmd = "(symbolp '" + s + ")";
//		System.out.println("Running command: " + cmd);
		String result = lisp.eval(cmd);
		return result.equalsIgnoreCase("T");
	}
}
