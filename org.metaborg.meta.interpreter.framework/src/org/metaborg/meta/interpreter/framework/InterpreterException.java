/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import org.spoofax.terms.TermFactory;

/**
 * @author vladvergu
 * 
 */
public class InterpreterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4486855230813687971L;

	private final String arrowname;
	private final Object term;

	public InterpreterException(String msg) {
		this(msg, null, null, null);
	}

	public InterpreterException(String msg, String arrowname, Object term) {
		this(msg, arrowname, term, null);
	}

	public InterpreterException(String msg, Throwable t) {
		super(msg, t);
		term = null;
		arrowname = null;
	}

	public InterpreterException(String msg, String arrowname, Object term,
			Throwable t) {
		super(msg + " in arrow: " + arrowname + " on term: "
				+ termToString(term), t);
		this.arrowname = arrowname;
		this.term = term;
	}

	private static String termToString(Object t) {
		if (t != null && t instanceof IConvertibleToStrategoTerm) {
			return ((IConvertibleToStrategoTerm) t).toStrategoTerm(
					new TermFactory()).toString(120);
		}
		return t + "";
	}

	public String getArrowName() {
		return arrowname;
	}

	public Object getTerm() {
		return term;
	}

}
