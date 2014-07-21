package org.metaborg.meta.interpreter.framework;

import java.util.Objects;

public abstract class CallTarget {

	protected final INode targetNode;

	public CallTarget(INode targetNode) {
		Objects.requireNonNull(targetNode);
		this.targetNode = targetNode;
	}

	public abstract AValue invoke();
	
	public INode getTargetNode() {
		return targetNode;
	}

}
