package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.source.SourceSection;

public abstract class ASortRuleCallNode extends DynSemNode {

	public ASortRuleCallNode(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(Object o, Object[] arguments);

}
