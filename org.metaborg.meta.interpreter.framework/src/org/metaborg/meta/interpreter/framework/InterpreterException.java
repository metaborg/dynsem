/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public class InterpreterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4486855230813687971L;

	public InterpreterException(String string) {
		super(string);
	}

	public InterpreterException(String string, Throwable t) {
		super(string, t);
	}

}
