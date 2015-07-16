/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 *
 */
public class InterpreterExitException extends InterpreterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1056463372291035421L;

	private int exitValue;

	public InterpreterExitException(String msg, String arrowname, Object term,
			Throwable t, int exitValue) {
		super(msg, arrowname, term, t);
		this.exitValue = exitValue;
	}

	
	public InterpreterExitException(Object term, Throwable t, int exitValue) {
		super(null, null, term, t);
	}

	public int getValue() {
		return exitValue;
	}
}
