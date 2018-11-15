package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.StatefulControlFlowException;

public final class LoopResultException extends StatefulControlFlowException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 390554962320678768L;

	public LoopResultException(Object thrown, Object[] components) {
		super(thrown, components);
	}

}
