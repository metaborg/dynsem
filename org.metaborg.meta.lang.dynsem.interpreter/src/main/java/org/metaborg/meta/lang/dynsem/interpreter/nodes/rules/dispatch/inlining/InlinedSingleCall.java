package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InlinedSingleCall extends InlinedCall {

	protected final CallTarget inlinedTarget;

	public InlinedSingleCall(SourceSection source, CallTarget target) {
		super(source);
		this.inlinedTarget = target;
	}

	@Specialization
	public RuleResult doCaching(Object[] callArgs,
			@Cached("createInlineableRule(inlinedTarget)") InlinedRuleWrap inlinedRule) {
		return inlinedRule.execute(callArgs);
	}

}
