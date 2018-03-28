package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;

public final class AbortedEvaluationException extends ControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6364830568195671697L;

	private final IApplTerm thrown;
	private final MaterializedFrame components; 
	
	public AbortedEvaluationException(IApplTerm thrown, MaterializedFrame components) {
		this.thrown = thrown;
		this.components = components;
	}

	public IApplTerm getThrown() {
		return thrown;
	}
	
	public MaterializedFrame getComponents() {
		return components;
	}
}
