package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.StatefulControlFlowException;

import com.oracle.truffle.api.frame.MaterializedFrame;

public final class LoopContinueException extends StatefulControlFlowException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8538019403992035650L;

	public LoopContinueException(Object thrown, Object[] components) {
		super(thrown, components);
	}

}
