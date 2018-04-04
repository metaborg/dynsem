package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.source.SourceSection;

public abstract class ChainedRulesNode extends DynSemNode {

	public ChainedRulesNode(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(Object[] arguments);

	public abstract int ruleCount();

	public static ChainedRulesNode createDeepFromRules(SourceSection source, RuleKind kind, String arrowName,
			Class<?> dispatchClass, Rule[] rules) {
		ChainedRulesNode seq = CallAltRuleNodeGen.create(source, dispatchClass, kind, arrowName);
		for (int i = rules.length - 1; i >= 0; i--) {
			Rule r = rules[i];
			seq = new RuleChainNode(r.getSourceSection(), r, seq);
		}
		return seq;
	}
	
//	public abstract void insertOrReplaceAltCall(ChainedRulesNode altCall);
	
//	public static ChainedRulesNode createShallowFromRules(SourceSection source, RuleKind kind, String arrowName,
//			Class<?> dispatchClass, Rule[] rules) {
//		// FIXME implement
//	}
}
