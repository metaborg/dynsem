package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.source.SourceSection;

public class JointRuleNode extends DynSemNode {

	@Child private RuleUnionNode mainRuleNode;
	@Child private ASortRuleCallNode sortRuleNode;

	public JointRuleNode(SourceSection source, RuleKind kind, String arrowName, Class<?> dispatchClass, Rule[] rules) {
		super(source);
		if (rules.length == 1) {
			this.mainRuleNode = new SingleRuleUnionNode(source, arrowName, dispatchClass, rules[0]);
		} else {
			this.mainRuleNode = new MultiRuleUnionNode(source, arrowName, dispatchClass, rules);
		}

		// only rules over constructors have a fallback
		if (kind == RuleKind.TERM || kind == RuleKind.ADHOC) {
			this.sortRuleNode = SortRuleCallNodeGen.create(source, arrowName);
		} else {
			this.sortRuleNode = new FailSortRuleCallNode(source);
		}
	}

	public RuleResult execute(Object[] arguments) {
		RuleResult res = null;
		boolean repeat = true;
		while (repeat) {
			try {
				try {
					return mainRuleNode.execute(arguments);
				} catch (PatternMatchFailure pmfx) {
					return sortRuleNode.execute(arguments[0], arguments);
				}
			} catch (RecurException recex) {
				repeat = true;
			}
		}
		assert res != null;
		return res;
	}

	public RuleUnionNode getUnionNode() {
		return mainRuleNode;
	}

	public ASortRuleCallNode getSortRuleNode() {
		return sortRuleNode;
	}

}
