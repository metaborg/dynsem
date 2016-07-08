package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.source.SourceSection;

public class PolymorphicUnionNode extends DynSemNode {

	protected final String arrowName;

	public PolymorphicUnionNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public RuleResult execute(Object[] arguments) {
		return getContext().getRuleRegistry().lookupRules(arrowName, arguments[0].getClass()).getJointNode()
				.execute(arguments);
	}

}
