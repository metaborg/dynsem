package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class UnstableDispatchNode extends DynSemNode {

	private final String arrowName;
	
	@Child private IndirectCallRulesNode rulesCall;
	@Child private DispatchSortRulesNode fallback;

	public UnstableDispatchNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
		this.rulesCall = new IndirectCallRulesNode(source);
		this.fallback = DispatchSortRulesNodeGen.create(source, arrowName);
	}

	public RuleResult execute(VirtualFrame frame, Class<?> dispatchClass, Object[] args) {
		RuleRoot[] rules = getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass);
		try {
			return rulesCall.execute(frame, rules, args);
		} catch (PremiseFailure pfx) {
			return fallback.execute(frame, args[0], args);
		}

	}

}
