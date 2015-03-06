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

	public InterpreterExitException(String msg, Throwable t, int exitValue) {
		super(msg, t);
		this.exitValue = exitValue;
	}

	public InterpreterExitException(Throwable t, int exitValue) {
		super(t);
	}

	public int getValue() {
		return exitValue;
	}
}
