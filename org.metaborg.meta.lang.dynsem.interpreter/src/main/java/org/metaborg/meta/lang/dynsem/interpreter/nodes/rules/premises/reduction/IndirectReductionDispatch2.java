package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IndirectReductionDispatch2 extends DynSemNode {

	protected final String arrowName;

	public IndirectReductionDispatch2(String arrowname, SourceSection source) {
		super(source);
		this.arrowName = arrowname;
	}

	public abstract RuleResult executeDispatch(VirtualFrame frame, Object[] args);

	public static class _Uninitialized extends IndirectReductionDispatch2 {

		public _Uninitialized(String arrowName, SourceSection source) {
			super(arrowName, source);
		}

		@Override
		public RuleResult executeDispatch(VirtualFrame frame, Object[] args) {
			CompilerDirectives.transferToInterpreter();
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);

			RuleRoot redTarget = getContext().getRuleRegistry().lookupRule(arrowName, redTerm.constructor(),
					redTerm.arity());
			return replace(
					new _Monomorphic(redTerm, NodeUtil.cloneNode(redTarget.getRule()), redTarget.getFrameDescriptor(),
							arrowName, getSourceSection())).executeDispatch(frame, args);

		}

	}

	public static class _Monomorphic extends IndirectReductionDispatch2 {

		private final ITerm redTerm;

		@Child private Rule rule;

		private final FrameDescriptor fd;

		public _Monomorphic(ITerm redTerm, Rule rule, FrameDescriptor fd, String arrowName, SourceSection source) {
			super(arrowName, source);
			this.redTerm = redTerm;
			this.rule = rule;
			this.fd = fd;
		}

		@Override
		public RuleResult executeDispatch(VirtualFrame frame, Object[] args) {
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);
			if (this.redTerm == redTerm) {
				return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
			} else {
				CompilerDirectives.transferToInterpreterAndInvalidate();
				return replace(new _Polymorphic(this.redTerm, rule, fd, arrowName, getSourceSection()))
						.executeDispatch(frame, args);
			}
		}
	}

	public static class _Polymorphic extends IndirectReductionDispatch2 {
		private final String constr;
		private final int arity;

		@Child private Rule rule;

		private final FrameDescriptor fd;

		public _Polymorphic(ITerm redTerm, Rule rule, FrameDescriptor fd, String arrowName, SourceSection source) {
			super(arrowName, source);
			this.constr = redTerm.constructor();
			this.arity = redTerm.arity();
			this.rule = rule;
			this.fd = fd;
		}

		@Override
		public RuleResult executeDispatch(VirtualFrame frame, Object[] args) {
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);
			if (redTerm.arity() == this.arity && redTerm.constructor().equals(this.constr)) {
				return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
			} else {
				CompilerDirectives.transferToInterpreterAndInvalidate();
				return replace(new _Generic(arrowName, getSourceSection())).executeDispatch(frame, args);
			}
		}
	}

	public class _Generic extends IndirectReductionDispatch2 {

		@Child private IndirectCallNode callNode;

		public _Generic(String arrowName, SourceSection source) {
			super(arrowName, source);
			this.callNode = IndirectCallNode.create();
		}

		@Override
		public RuleResult executeDispatch(VirtualFrame frame, Object[] args) {
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);

			RuleRoot redTarget = getContext().getRuleRegistry().lookupRule(arrowName, redTerm.constructor(),
					redTerm.arity());

			CallTarget ct = redTarget.getCallTarget();

			return (RuleResult) callNode.call(frame, ct, args);
		}
	}
}
