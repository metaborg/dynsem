package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;

public class OverloadedRule extends Rule {

	@CompilationFinal protected OverloadedRule next;
	@Child protected InlinedRuleAdapter rule;

	private final BranchProfile ruleFailedTaken = BranchProfile.create();

	public OverloadedRule(InlinedRuleAdapter rule) {
		super(rule.getSourceSection(), rule.getRule().getFrameDescriptor(), rule.getRule().getKey());
		this.rule = rule;
	}

	public void addNext(InlinedRuleAdapter adaptedRule) {
		if (next != null) {
			next.addNext(adaptedRule);
		} else {
			next = new OverloadedRule(adaptedRule);
		}
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		try {
			return (RuleResult) rule.execute(frame);
		} catch (PremiseFailure pfx) {
			ruleFailedTaken.enter();
			if (next != null) {
				return next.execute(frame);
			} else {
				throw pfx;
			}
		}
	}

}
