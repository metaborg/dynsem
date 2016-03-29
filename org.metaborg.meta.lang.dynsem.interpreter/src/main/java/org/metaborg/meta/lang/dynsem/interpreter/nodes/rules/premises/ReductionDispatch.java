package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.IndirectReductionDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.IndirectReductionDispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.PremiseLhs;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ReductionDispatch extends Node {

	@Child protected PremiseLhs lhs;

	public ReductionDispatch(PremiseLhs lhs, SourceSection source) {
		super(source);
		this.lhs = lhs;
	}

	public abstract RuleResult execute(VirtualFrame frame);

	public static class InlineableReductionDispatch extends ReductionDispatch {

		private final String conName;
		private final int arity;
		private final String arrowName;

		public InlineableReductionDispatch(String conName, int arity,
				String arrowName, PremiseLhs lhs, SourceSection source) {
			super(lhs, source);
			this.conName = conName;
			this.arrowName = arrowName;
			this.arity = arity;
		}

		@Override
		public RuleResult execute(VirtualFrame frame) {
			// CompilerDirectives.transferToInterpreterAndInvalidate();
			RuleRoot rr = DynSemContext.LANGUAGE.getContext().getRuleRegistry()
					.lookupRule(arrowName, conName, arity);
			return replace(
					new InlinedReductionDispatch(NodeUtil.cloneNode(lhs),
							NodeUtil.cloneNode(rr.getRule()), rr
									.getFrameDescriptor(), getSourceSection()))
					.execute(frame);
		}

	}

	public static class InlinedReductionDispatch extends ReductionDispatch {

		@Child protected Rule rule;
		private final FrameDescriptor fd;

		public InlinedReductionDispatch(PremiseLhs lhs, Rule rule,
				FrameDescriptor fd, SourceSection source) {
			super(lhs, source);
			this.rule = rule;
			this.fd = fd;
		}

		public RuleResult execute(VirtualFrame frame) {
			Object[] args = lhs.executeObjectArray(frame);
			return rule.execute(Truffle.getRuntime().createVirtualFrame(args,
					fd));
		}

	}

	public static class DynamicReductionDispatch extends ReductionDispatch {

		@Child protected IndirectReductionDispatch dispatcher;

		public DynamicReductionDispatch(PremiseLhs lhs, String arrowName,
				SourceSection source) {
			super(lhs, source);
			this.dispatcher = IndirectReductionDispatchNodeGen.create(
					arrowName, source);
		}

		@Override
		public RuleResult execute(VirtualFrame frame) {
			Object[] args = lhs.executeObjectArray(frame);
			return dispatcher.executeDispatch(frame, args[0], args);
		}

	}

}
