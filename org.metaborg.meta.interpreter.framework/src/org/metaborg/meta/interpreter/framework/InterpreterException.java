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

	public InterpreterException(String msg) {
		super(msg);
	}

	public InterpreterException(String msg, Throwable t) {
		super(msg, t);
	}

	public InterpreterException(Throwable t) {
		super(t);
	}

}
