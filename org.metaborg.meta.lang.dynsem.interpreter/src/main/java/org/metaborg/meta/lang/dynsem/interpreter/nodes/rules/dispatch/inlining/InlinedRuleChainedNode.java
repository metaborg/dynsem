package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public final class InlinedRuleChainedNode extends DynSemNode {

	private final FrameDescriptor inlinedRuleFrameDescriptor;
	@Child protected RuleNode inlinedRule;
	@Child protected InlinedRuleChainedNode next;

	public InlinedRuleChainedNode(SourceSection source, FrameDescriptor inlinedRuleFrameDescriptor,
			RuleNode inlinedRule, InlinedRuleChainedNode next) {
		super(source);
		this.inlinedRuleFrameDescriptor = inlinedRuleFrameDescriptor;
		this.inlinedRule = inlinedRule;
		this.next = next;
	}

	public RuleResult execute(Object[] args, boolean deepExecAllowed) {
		try {
			return inlinedRule.execute(Truffle.getRuntime().createVirtualFrame(args, inlinedRuleFrameDescriptor));
		} catch (PremiseFailureException pmfex) {
			if (next != null && deepExecAllowed) {
				return next.execute(args, true);
			}
			throw pmfex;
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
