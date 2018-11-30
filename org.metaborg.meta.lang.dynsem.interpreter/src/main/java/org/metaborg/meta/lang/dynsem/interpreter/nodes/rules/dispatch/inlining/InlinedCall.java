package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRootNode;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InlinedCall extends DynSemNode {

	public InlinedCall(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(Object[] callArgs);

	protected final InlinedRuleWrap createInlineableRule(CallTarget target) {
		CompilerAsserts.neverPartOfCompilation("May not inline rules from compiled code");
		DynSemContext ctx = getContext();
		IRuleRegistry rr = ctx.getRuleRegistry();
		RuleRootNode root = rr.lookupRuleRoot(target);
		FrameDescriptor fd = root.getFrameDescriptor();
		return new InlinedRuleWrap(fd,
				RuleNode.create(rr.getLanguage(), root.getRuleSourceTerm(), fd, ctx.getTermRegistry()));
	}

	public static InlinedCall create(SourceSection source, CallTarget[] targets) {
		if (targets.length > 1) {
			return new InlinedMultiCall(source, targets);
		} else {
			return InlinedSingleCallNodeGen.create(source, targets[0]);
		}
	}

	public static class InlinedRuleWrap extends Node {

		private final FrameDescriptor fd;
		@Child private RuleNode rule;

		public InlinedRuleWrap(FrameDescriptor fd, RuleNode rule) {
			this.fd = fd;
			this.rule = rule;
		}

		public RuleResult execute(Object[] callArgs) {
			return rule.execute(Truffle.getRuntime().createVirtualFrame(callArgs, fd));
		}

	}

}
