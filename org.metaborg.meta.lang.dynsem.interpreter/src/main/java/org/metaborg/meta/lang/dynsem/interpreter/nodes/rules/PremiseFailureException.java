package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class PremiseFailureException extends ControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4191870489590328475L;

	public static final PremiseFailureException SINGLETON = new PremiseFailureException();

	private PremiseFailureException() {
	}

}
