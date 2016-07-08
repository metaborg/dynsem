package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RuleUnionNode extends DynSemNode {

	private final String arrowName;
	private final Class<?> dispatchClass;

	public RuleUnionNode(SourceSection source, String arrowName, Class<?> dispatchClass) {
		super(source);
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
	}

	public abstract RuleResult execute(Object[] arguments);

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + arrowName + "->";
	}

	public abstract List<Rule> getRules();
}
