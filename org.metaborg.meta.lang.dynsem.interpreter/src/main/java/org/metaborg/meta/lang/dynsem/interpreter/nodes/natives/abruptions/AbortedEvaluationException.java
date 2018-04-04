package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.StatefulControlFlowException;

public final class AbortedEvaluationException extends StatefulControlFlowException {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -283186208089661555L;

	public AbortedEvaluationException(Object thrown, Object[] components) {
		super(thrown, components);
	}

}
