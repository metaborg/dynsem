package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleUnionNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleUnionRoot;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class SortRulesUnionNode extends DynSemNode {

	private final String arrowName;

	public SortRulesUnionNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Object o, Object[] arguments);

	@Specialization(limit = "1", guards = "o.getClass() != sortDispatchClass")
	public RuleResult doFallback(IApplTerm o, Object[] arguments,
			@Cached("o.getSortClass()") Class<?> sortDispatchClass,
			@Cached("createSortUnionNode(o, sortDispatchClass)") RuleUnionNode sortRuleUnion) {
		return sortRuleUnion.execute(arguments);
	}

	@Specialization
	public RuleResult doNoFallback(Object o, Object[] arguments) {
		if(DynSemContext.LANGUAGE.isFullBacktrackingEnabled()) {
			throw PatternMatchFailure.INSTANCE;
		} else {
			throw ReductionFailure.INSTANCE;
		}
	}

	protected final RuleUnionNode createSortUnionNode(IApplTerm o, Class<?> sortDispatchClass) {
		RuleUnionRoot root = getContext().getRuleRegistry().lookupRules(arrowName, sortDispatchClass);
		return NodeUtil.cloneNode(root.getUnionNode());
	}

}
