package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class CaseFailure extends ControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5339905640325022207L;

	public static final CaseFailure INSTANCE = new CaseFailure();

	private CaseFailure() {
	}

}
