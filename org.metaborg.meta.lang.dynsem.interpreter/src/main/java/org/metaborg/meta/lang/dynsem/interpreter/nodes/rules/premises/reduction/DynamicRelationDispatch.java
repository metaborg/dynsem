package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynamicRelationDispatch extends RelationDispatch {

	protected final String arrowName;

	public DynamicRelationDispatch(String arrowname, SourceSection source) {
		super(source);
		this.arrowName = arrowname;
	}

	public static class _Uninitialized extends DynamicRelationDispatch {

		public _Uninitialized(String arrowName, SourceSection source) {
			super(arrowName, source);
		}

		@Override
		public RuleResult execute(VirtualFrame frame, Object[] args) {
			CompilerDirectives.transferToInterpreter();
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);

			RuleRoot redTarget = getContext().getRuleRegistry().lookupRule(arrowName, redTerm.constructor(),
					redTerm.arity());
			return replace(
					new _Monomorphic(redTerm, NodeUtil.cloneNode(redTarget.getRule()), redTarget.getFrameDescriptor(),
							arrowName, getSourceSection())).execute(frame, args);

		}

	}

	public static class _Monomorphic extends DynamicRelationDispatch {

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
		public RuleResult execute(VirtualFrame frame, Object[] args) {
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);
			if (this.redTerm == redTerm) {
				return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
			} else {
				CompilerDirectives.transferToInterpreterAndInvalidate();
				return replace(new _Polymorphic(this.redTerm, rule, fd, arrowName, getSourceSection())).execute(frame,
						args);
			}
		}
	}

	public static class _Polymorphic extends DynamicRelationDispatch {

		private final Class<? extends ITerm> termClazz;

		@Child private Rule rule;

		private final FrameDescriptor fd;

		public _Polymorphic(ITerm redTerm, Rule rule, FrameDescriptor fd, String arrowName, SourceSection source) {
			super(arrowName, source);
			this.termClazz = redTerm.getClass();
			this.rule = rule;
			this.fd = fd;
		}

		@Override
		public RuleResult execute(VirtualFrame frame, Object[] args) {
			// TODO this kind of check should actually be generated to be specific to the LHS term
			assert ITerm.class.isAssignableFrom(termClazz) || PersistentList.class.isAssignableFrom(termClazz);
			if (termClazz == args[0].getClass()) {
				return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
			} else {
				CompilerDirectives.transferToInterpreterAndInvalidate();
				return replace(new _Generic(arrowName, getSourceSection())).execute(frame, args);
			}
		}
	}

	public static class _Generic extends DynamicRelationDispatch {

		@Child private IndirectCallNode callNode;

		public _Generic(String arrowName, SourceSection source) {
			super(arrowName, source);
			this.callNode = IndirectCallNode.create();
		}

		@Override
		public RuleResult execute(VirtualFrame frame, Object[] args) {
			ITerm redTerm = BuiltinTypesGen.asITerm(args[0]);

			RuleRoot redTarget = getContext().getRuleRegistry().lookupRule(arrowName, redTerm.constructor(),
					redTerm.arity());

			CallTarget ct = redTarget.getCallTarget();

			return (RuleResult) callNode.call(frame, ct, args);
		}
	}
}
