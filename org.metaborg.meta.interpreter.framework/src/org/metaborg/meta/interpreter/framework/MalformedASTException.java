/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public class MalformedASTException extends InterpreterException {

	public MalformedASTException() {
		this("Rewriting exception");
	}

	public MalformedASTException(String s) {
		super(s);
	}

	public MalformedASTException(Exception ex) {
		super("Rewriting exception", null, ex);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8100998611899130326L;

}
