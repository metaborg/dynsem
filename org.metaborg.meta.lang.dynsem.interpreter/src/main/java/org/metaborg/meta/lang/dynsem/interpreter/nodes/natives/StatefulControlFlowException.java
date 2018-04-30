package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.ControlFlowException;

public abstract class StatefulControlFlowException extends ControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 562405562797055306L;

	private final Object thrown;
	@CompilationFinal(dimensions = 1) private final Object[] components;

	public StatefulControlFlowException(Object thrown, Object[] components) {
		this.thrown = thrown;
		this.components = components;
	}

	public final Object getThrown() {
		return thrown;
	}

	public final Object[] getComponents() {
		return components;
	}
}
