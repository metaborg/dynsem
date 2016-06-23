package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class SortRuleCallNode extends ASortRuleCallNode {

	private final String arrowName;

	public SortRuleCallNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	@Specialization(limit = "1", guards = "o.getSortClass() == sortDispatchClass")
	public RuleResult doFallback(IApplTerm o, Object[] arguments,
			@Cached("o.getSortClass()") Class<?> sortDispatchClass,
			@Cached("createSortUnionNode(o, sortDispatchClass)") JointRuleNode sortJointRule) {
		return sortJointRule.execute(arguments);
	}

	protected final JointRuleNode createSortUnionNode(IApplTerm o, Class<?> sortDispatchClass) {
		JointRuleRoot root = getContext().getRuleRegistry().lookupRules(arrowName, sortDispatchClass);
		return NodeUtil.cloneNode(root.getJointNode());
	}

}
