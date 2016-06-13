package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public class DirectCallRulesNode extends DynSemNode {

	@Children private final RuleRoot[] rules;

	private DirectCallRulesNode(SourceSection source, RuleRoot[] rules) {
		super(source);
		this.rules = rules;
	}

	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame, Object[] args) {
		for (int i = 0; i < rules.length; i++) {
			try {
				return rules[i].execute(frame);
			} catch (PremiseFailure pfx) {
				;
			}
		}
		throw PremiseFailure.INSTANCE;
	}

	public static DirectCallRulesNode create(SourceSection source, RuleRoot[] rules) {
		CompilerAsserts.neverPartOfCompilation();
		RuleRoot[] clonedRules = new RuleRoot[rules.length];
		for (int i = 0; i < clonedRules.length; i++) {
			clonedRules[i] = NodeUtil.cloneNode(rules[i]);
		}
		return new DirectCallRulesNode(source, clonedRules);
	}

}
