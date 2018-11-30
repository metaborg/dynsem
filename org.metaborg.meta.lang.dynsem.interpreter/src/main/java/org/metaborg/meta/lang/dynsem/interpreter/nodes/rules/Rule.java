package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends DynSemNode {

	public Rule(SourceSection source) {
		super(source);
	}

	public abstract RuleResult evaluateRule(VirtualFrame frame);

}
