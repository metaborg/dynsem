package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.StatefulControlFlowException;

public final class LoopBreakException extends StatefulControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2644872422132670754L;

	public LoopBreakException(Object thrown, Object[] components) {
		super(thrown, components);
	}

}
