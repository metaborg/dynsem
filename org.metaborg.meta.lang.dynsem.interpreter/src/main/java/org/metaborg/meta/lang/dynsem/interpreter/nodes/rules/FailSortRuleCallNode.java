package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.source.SourceSection;

public class FailSortRuleCallNode extends ASortRuleCallNode {

	public FailSortRuleCallNode(SourceSection source) {
		super(source);
	}

	@Override
	public RuleResult execute(Object o, Object[] arguments) {
		if (DynSemContext.LANGUAGE.isFullBacktrackingEnabled()) {
			throw PatternMatchFailure.INSTANCE;
		} else {
			throw new ReductionFailure("No rules applicable for term " + o, InterpreterUtils.createStacktrace());
		}
	}

}
