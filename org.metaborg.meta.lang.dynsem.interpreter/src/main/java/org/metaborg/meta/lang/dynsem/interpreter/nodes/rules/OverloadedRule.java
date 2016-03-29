package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.utilities.BranchProfile;

public class OverloadedRule extends Rule {

	@CompilationFinal protected OverloadedRule next;
	@Child protected InlinedRuleAdapter rule;

	private final BranchProfile ruleFailedTaken = BranchProfile.create();

	private String constr;
	private String name;
	private int arity;

	public OverloadedRule(InlinedRuleAdapter rule) {
		super(rule.getSourceSection());
		this.constr = rule.getRule().getConstructor();
		this.name = rule.getRule().getName();
		this.arity = rule.getRule().getArity();
		this.rule = rule;
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public String getConstructor() {
		return constr;
	}

	@Override
	public String getName() {
		return name;
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
