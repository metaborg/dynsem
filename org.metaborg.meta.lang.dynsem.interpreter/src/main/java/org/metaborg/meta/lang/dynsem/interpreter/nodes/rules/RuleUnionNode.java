package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.SortRulesUnionNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.SortRulesUnionNodeGen;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RuleUnionNode extends DynSemNode {

	@Children private final Rule[] rules;
	@Child private SortRulesUnionNode fallbackRulesNode;

	public RuleUnionNode(SourceSection source, String arrowName, Rule[] rules) {
		super(source);
		this.rules = rules;
		this.fallbackRulesNode = SortRulesUnionNodeGen.create(source, arrowName);
	}

	public RuleResult execute(final Object[] arguments) {
		try {
			return executeMainRules(arguments);
		} catch (PremiseFailure pfx) {
			return fallbackRulesNode.execute(arguments[0], arguments);
		}
	}

	public Rule[] getRules() {
		return rules;
	}
	
	public SortRulesUnionNode getSortRulesNode() {
		return fallbackRulesNode;
	}

	@ExplodeLoop
	private RuleResult executeMainRules(final Object[] arguments) {
		for (int i = 0; i < rules.length; i++) {
			try {
				return rules[i]
						.execute(Truffle.getRuntime().createVirtualFrame(arguments, rules[i].getFrameDescriptor()));
			} catch (PremiseFailure pfx) {
				;
			}
		}
		throw PremiseFailure.INSTANCE;
	}
}
