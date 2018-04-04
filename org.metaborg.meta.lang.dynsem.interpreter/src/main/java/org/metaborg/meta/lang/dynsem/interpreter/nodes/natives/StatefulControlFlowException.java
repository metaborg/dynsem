package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import com.oracle.truffle.api.nodes.ControlFlowException;

public abstract class StatefulControlFlowException extends ControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 562405562797055306L;

	private final Object thrown;
	private final Object[] components;

	public StatefulControlFlowException(Object thrown, Object[] components) {
		this.thrown = thrown;
		this.components = components;
	}

	public Object getThrown() {
		return thrown;
	}

	public Object[] getComponents() {
		return components;
	}
}
