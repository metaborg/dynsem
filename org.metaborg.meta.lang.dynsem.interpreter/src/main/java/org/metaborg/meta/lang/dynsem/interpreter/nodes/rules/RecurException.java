package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class RecurException extends ControlFlowException {

	public static final RecurException INSTANCE = new RecurException();

	/**
	 * 
	 */
	private static final long serialVersionUID = -6715395216181319075L;

	private RecurException() {
	}

}
