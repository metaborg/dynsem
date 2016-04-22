package org.metaborg.meta.lang.dynsem.interpreter;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class PremiseFailure extends ControlFlowException {

	public static final PremiseFailure INSTANCE = new PremiseFailure();

	private PremiseFailure() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6517293468155311067L;

}
