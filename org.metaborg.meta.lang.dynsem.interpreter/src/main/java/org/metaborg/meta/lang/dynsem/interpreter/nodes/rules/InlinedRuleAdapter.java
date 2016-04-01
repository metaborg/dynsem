package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public class InlinedRuleAdapter extends Node {

	@Child protected Rule rule;
	private final FrameDescriptor fd;

	public InlinedRuleAdapter(Rule rule, FrameDescriptor fd) {
		super(rule.getSourceSection());
		this.rule = rule;
		this.fd = fd;
	}

	public RuleResult execute(VirtualFrame frame) {
		return rule.execute(Truffle.getRuntime().createVirtualFrame(frame.getArguments(), fd));
	}

	public Rule getRule() {
		return rule;
	}

}
