package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * {@link ControlFlowException} to signal that a reduction has failed. In contrast to {@link PatternMatchFailure} this
 * exception is raised when an application of a rule has failed.
 * 
 * @author vladvergu
 *
 */
public class ReductionFailure extends RuntimeException {

	public ReductionFailure(String trace, Throwable t) {
		super(trace, t);
	}

	public ReductionFailure(String message, String trace) {
		super(message + "\n" + trace);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3427027667192711474L;

}
