package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class StableDispatchNode extends DynSemNode {

	@Child private DirectCallRulesNode rulesCall;
	@Child private DispatchSortRulesNode fallback;

	public StableDispatchNode(SourceSection source, String arrowName, RuleRoot[] rules) {
		super(source);
		this.rulesCall = DirectCallRulesNode.create(source, rules);
		this.fallback = DispatchSortRulesNodeGen.create(source, arrowName);
	}

	public RuleResult execute(VirtualFrame frame, Object[] args) {
		try {
			// try the main rules
			return rulesCall.execute(frame, args);
		} catch (PremiseFailure pfx) {
			// no rule succeeded so try a fallback
			return fallback.execute(frame, args[0], args);
		}
	}

}
