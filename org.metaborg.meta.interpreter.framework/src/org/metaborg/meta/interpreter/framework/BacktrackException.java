/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * @author vladvergu
 *
 */
public class BacktrackException extends ControlFlowException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818479153890490970L;

	public static final BacktrackException SINGLETON = new BacktrackException();

	/* Prevent instantiation from outside. */
	private BacktrackException() {
	}

}
