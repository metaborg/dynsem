package org.metaborg.meta.lang.dynsem.interpreter;

public class InterpreterException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2720402112231306295L;

	public InterpreterException(String msg) {
		super(msg);
	}

	public InterpreterException(Throwable cause) {
		super(cause);
	}

	public InterpreterException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
