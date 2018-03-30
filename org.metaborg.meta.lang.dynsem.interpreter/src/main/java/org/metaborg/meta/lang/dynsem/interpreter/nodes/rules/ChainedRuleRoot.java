package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ChainedRuleRoot extends DynSemRootNode {

	@Child private ChainedRulesNode rules;

	public ChainedRuleRoot(DynSemLanguage lang, SourceSection source, RuleKind kind, String arrowName,
			Class<?> dispatchClass, Rule[] rules) {
		super(lang);
		this.rules = ChainedRulesNode.createFromRules(source, kind, arrowName, dispatchClass, rules);

		Truffle.getRuntime().createCallTarget(this);
	}

	public ChainedRulesNode getChainedRules() {
		return rules;
	}
	
	@Override
	public RuleResult execute(VirtualFrame frame) {
		return rules.execute(frame.getArguments());
	}

	@TruffleBoundary
	public String toString() {
		return rules.toString();
	}

}
