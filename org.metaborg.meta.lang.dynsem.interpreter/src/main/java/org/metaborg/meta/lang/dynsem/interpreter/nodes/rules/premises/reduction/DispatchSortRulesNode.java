package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchSortRulesNode extends DynSemNode {

	private final String arrowName;

	public DispatchSortRulesNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(VirtualFrame frame, Object o, Object[] args);

	@Specialization
	public RuleResult doFallback(VirtualFrame frame, IApplTerm o, Object[] args,
			@Cached("o.getSortClass()") Class<?> sortDispatchClass,
			@Cached("createRulesCallNode(o, sortDispatchClass)") DirectCallRulesNode rulesCall) {
		return rulesCall.execute(frame, args);
	}

	@Specialization
	public RuleResult doNoFallback(VirtualFrame frame, Object o, Object[] args) {
		throw PremiseFailure.INSTANCE;
	}

	protected final DirectCallRulesNode createRulesCallNode(IApplTerm o, Class<?> sortDispatchClass) {
		return DirectCallRulesNode.create(getSourceSection(),
				getContext().getRuleRegistry().lookupRules(arrowName, sortDispatchClass));
	}

}
