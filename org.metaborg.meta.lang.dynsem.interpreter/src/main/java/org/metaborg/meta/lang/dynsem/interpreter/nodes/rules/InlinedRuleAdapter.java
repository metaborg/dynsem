package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public class InlinedRuleAdapter extends Node {

	@Child protected Rule rule;

	public InlinedRuleAdapter(Rule rule) {
		super(rule.getSourceSection());
		this.rule = rule;
	}

	public RuleResult execute(VirtualFrame frame) {
		return rule.execute(Truffle.getRuntime().createVirtualFrame(frame.getArguments(), rule.getFrameDescriptor()));
	}

	public Rule getRule() {
		return rule;
	}

}
