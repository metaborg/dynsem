package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InlinedDispatchChainedNode extends DynSemNode {

	private final FrameDescriptor inlinedRuleFrameDescriptor;
	@Child protected RuleNode inlinedRule;
	@Child protected InlinedDispatchChainedNode next;

	public InlinedDispatchChainedNode(SourceSection source, FrameDescriptor inlinedRuleFrameDescriptor,
			RuleNode inlinedRule, InlinedDispatchChainedNode next) {
		super(source);
		this.inlinedRuleFrameDescriptor = inlinedRuleFrameDescriptor;
		this.inlinedRule = inlinedRule;
		this.next = next;
	}

	public abstract RuleResult execute(Object[] args);

	@Specialization(guards = "next == null")
	protected RuleResult doShallow(Object[] args) {
		return inlinedRule.execute(Truffle.getRuntime().createVirtualFrame(args, inlinedRuleFrameDescriptor));
	}

	@Specialization(replaces = "doShallow")
	protected RuleResult doDeep(Object[] args) {
		try {
			return inlinedRule.execute(Truffle.getRuntime().createVirtualFrame(args, inlinedRuleFrameDescriptor));
		} catch (PremiseFailureException pmfex) {
			return next.execute(args);
		}
	}

	protected final int length() {
		if (next == null) {
			return 1;
		} else {
			return 1 + next.length();
		}
	}

}
