package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

/**
 * An abstract class to aggregate multiple rules. Existence of multiple rules is typically the case with overloaded
 * rules. Instances (of descendants) of this node can be used to aggregate these overloaded rules.
 * 
 * 
 * @author vladvergu
 *
 */
public abstract class RuleSetNode extends DynSemNode {

	private final String arrowName;
	private final Class<?> dispatchClass;

	public RuleSetNode(SourceSection source, String arrowName, Class<?> dispatchClass) {
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
