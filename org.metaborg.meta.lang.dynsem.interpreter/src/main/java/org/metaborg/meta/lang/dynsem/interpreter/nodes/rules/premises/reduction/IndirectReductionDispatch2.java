package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
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

public abstract class IndirectReductionDispatch2 extends Node {

	private final Node contextNode;
	@CompilationFinal private DynSemContext cachedContext;

	protected final String arrowName;

	public IndirectReductionDispatch2(String arrowname, SourceSection source) {
		super(source);
		this.arrowName = arrowname;
		this.contextNode = DynSemContext.LANGUAGE.createFindContextNode0();
	}

	protected DynSemContext getContext() {
		if (cachedContext == null) {
			cachedContext = DynSemContext.LANGUAGE.findContext0(contextNode);
		}
		return cachedContext;
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

			return replace(new _Monomorphic(redTerm, redTarget, arrowName, getSourceSection())).executeDispatch(frame,
					args);

		}

	}

	public static class _Monomorphic extends IndirectReductionDispatch2 {

		private final ITerm redTerm;
		@Child private Rule rule;
		private final FrameDescriptor fd;

		public _Monomorphic(ITerm redTerm, RuleRoot redTarget, String arrowName, SourceSection source) {
			super(arrowName, source);
			this.redTerm = redTerm;
			this.rule = NodeUtil.cloneNode(redTarget.getRule());
			this.fd = redTarget.getFrameDescriptor();
		}

		@Override
		public RuleResult executeDispatch(VirtualFrame frame, Object[] args) {
			if (redTerm == args[0]) {
				return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
			} else {
				CompilerDirectives.transferToInterpreter();
				return replace(new _Generic(arrowName, getSourceSection())).executeDispatch(frame, args);
			}
		}

	}

	public class _Generic extends IndirectReductionDispatch2 {

		private final IndirectCallNode callNode;

		public _Generic(String arrowName, SourceSection source) {
			super(arrowName, source);
			this.callNode = IndirectCallNode.create();
		}

		@Override
		public RuleResult executeDispatch(VirtualFrame frame, Object[] args) {
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);

			RuleRoot redTarget = getContext().getRuleRegistry().lookupRule(arrowName, redTerm.constructor(),
					redTerm.arity());

			CallTarget ct = Truffle.getRuntime().createCallTarget(redTarget);

			return (RuleResult) callNode.call(frame, ct, args);
		}
	}
}
