package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

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
			@Cached("createRuleUnionNode(o, sortDispatchClass)") RuleUnionNode sortJointRule) {
		try {
			return sortJointRule.execute(arguments);
		} catch (PatternMatchFailure pmfx) {
			if (DynSemContext.LANGUAGE.isFullBacktrackingEnabled()) {
				throw pmfx;
			} else {
				throw new ReductionFailure("No rules applicable for term " + o, InterpreterUtils.createStacktrace());
			}
		}
	}

	protected final RuleUnionNode createRuleUnionNode(IApplTerm o, Class<?> sortDispatchClass) {
		JointRuleRoot root = getContext().getRuleRegistry().lookupRules(arrowName, sortDispatchClass);
		return NodeUtil.cloneNode(root.getJointNode().getUnionNode());
	}

}
