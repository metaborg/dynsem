package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.nodes.Node;

/**
 * {@link ControlFlowException} to signal that a reduction has failed. In contrast to {@link PatternMatchFailure} this
 * exception is raised when an application of a rule has failed.
 * 
 * @author vladvergu
 *
 */
public class ReductionFailure extends RuntimeException implements TruffleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3427027667192711474L;

	private final Node location;

	@TruffleBoundary
	public ReductionFailure(String message, String trace, Node location) {
		super(message + "\n" + trace);
		this.location = location;
	}


	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}

	@Override
	public Node getLocation() {
		return location;
	}

}
