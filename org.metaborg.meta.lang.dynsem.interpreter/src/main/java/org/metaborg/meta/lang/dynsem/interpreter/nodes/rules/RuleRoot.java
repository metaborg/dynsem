package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class RuleRoot extends RootNode {

	@Child protected Rule rule;

	public RuleRoot(Rule rule) {
		super(DynSemLanguage.class, rule.getSourceSection(), rule.getFrameDescriptor());
		this.rule = rule;
		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		return rule.execute(frame);
	}

	public Rule getRule() {
		return rule;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "RuleRoot: " + rule.toString();
	}

}
