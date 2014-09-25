/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public class RewritingException extends InterpreterException {

	public RewritingException() {
		this("Rewriting exception");
	}

	public RewritingException(String s) {
		super(s);
	}

	public RewritingException(Exception ex) {
		super("Rewriting exception", ex);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8100998611899130326L;

}
