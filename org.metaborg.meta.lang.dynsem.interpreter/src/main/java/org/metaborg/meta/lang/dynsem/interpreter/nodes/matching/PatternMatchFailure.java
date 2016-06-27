package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * {@link ControlFlowException} to signal that a pattern match application has failed.
 * 
 * @author vladvergu
 *
 */
public class PatternMatchFailure extends ControlFlowException {

	public static final PatternMatchFailure INSTANCE = new PatternMatchFailure();

	private PatternMatchFailure() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4485713452349320066L;

}
