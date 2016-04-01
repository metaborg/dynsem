package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.IndirectReductionDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.IndirectReductionDispatch2;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.IndirectReductionDispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.RelationAppLhs;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import trans.pp_type_0_0;
import trans.trans;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RelationDispatch extends Node {

	@Child protected RelationAppLhs lhs;

	public static RelationDispatch create(IStrategoAppl reads, IStrategoAppl source, IStrategoAppl arrow,
			FrameDescriptor fd) {

		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 2);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		IStrategoAppl lhsT = Tools.applAt(source, 0);
		IStrategoConstructor lhsC = lhsT.getConstructor();
		if (lhsC.getName().equals("Con") && lhsC.getArity() == 2) {
			// static dispatch
			return new InlineableRelationDispatch(Tools.stringAt(lhsT, 0).stringValue(), Tools.listAt(lhsT, 1).size(),
					arrowName, RelationAppLhs.create(reads, source, fd), SourceSectionUtil.fromStrategoTerm(source));
		} else if (lhsC.getName().equals("ListSource") && lhsC.getArity() == 2) {
			String key = "_" + Tools.asJavaString(pp_type_0_0.instance.invoke(trans.init(), Tools.termAt(lhsT, 1)));
			ITermFactory tf = new TermFactory();

			IStrategoAppl newSource = tf.makeAppl(source.getConstructor(), new IStrategoTerm[] { Tools.termAt(lhsT, 0),
					Tools.termAt(source, 1) });

			return new ListRelationDispatch(key, arrowName, RelationAppLhs.create(reads, newSource, fd),
					SourceSectionUtil.fromStrategoTerm(lhsT));
		} else {
			// dynamic dispatch
			return new DynamicRelationDispatch(RelationAppLhs.create(reads, source, fd), arrowName,
					SourceSectionUtil.fromStrategoTerm(source));
		}
	}

	public RelationDispatch(RelationAppLhs lhs, SourceSection source) {
		super(source);
		this.lhs = lhs;
	}

	public abstract RuleResult execute(VirtualFrame frame);

	public static class InlineableRelationDispatch extends RelationDispatch {

		private final String conName;
		private final int arity;
		private final String arrowName;

		public InlineableRelationDispatch(String conName, int arity, String arrowName, RelationAppLhs lhs,
				SourceSection source) {
			super(lhs, source);
			this.conName = conName;
			this.arrowName = arrowName;
			this.arity = arity;
		}

		@Override
		public RuleResult execute(VirtualFrame frame) {
//			CompilerDirectives.transferToInterpreter();
			RuleRoot rr = DynSemContext.LANGUAGE.getContext().getRuleRegistry().lookupRule(arrowName, conName, arity);
			return replace(
					new InlinedRelationDispatch(NodeUtil.cloneNode(lhs), NodeUtil.cloneNode(rr.getRule()), rr
							.getFrameDescriptor(), getSourceSection())).execute(frame);
		}

	}

	public static class ListRelationDispatch extends RelationDispatch {

		private final String arrowName;
		private final String ruleKey;

		public ListRelationDispatch(String ruleKey, String arrowName, RelationAppLhs lhs, SourceSection source) {
			super(lhs, source);
			this.arrowName = arrowName;
			this.ruleKey = ruleKey;
		}

		@Override
		public RuleResult execute(VirtualFrame frame) {
//			CompilerDirectives.transferToInterpreter();
			RuleRoot rr = DynSemContext.LANGUAGE.getContext().getRuleRegistry().lookupRule(arrowName, ruleKey, 1);
			return replace(
					new InlinedRelationDispatch(NodeUtil.cloneNode(lhs), NodeUtil.cloneNode(rr.getRule()), rr
							.getFrameDescriptor(), getSourceSection())).execute(frame);
		}

	}

	public static class InlinedRelationDispatch extends RelationDispatch {

		@Child protected Rule rule;
		private final FrameDescriptor fd;

		public InlinedRelationDispatch(RelationAppLhs lhs, Rule rule, FrameDescriptor fd, SourceSection source) {
			super(lhs, source);
			this.rule = rule;
			this.fd = fd;
		}

		public RuleResult execute(VirtualFrame frame) {
			Object[] args = lhs.executeObjectArray(frame);
			return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
		}

	}

	public static class DynamicRelationDispatch extends RelationDispatch {

		@Child protected IndirectReductionDispatch2 dispatcher;

		public DynamicRelationDispatch(RelationAppLhs lhs, String arrowName, SourceSection source) {
			super(lhs, source);
//			this.dispatcher = IndirectReductionDispatchNodeGen.create(arrowName, source);
			this.dispatcher = new IndirectReductionDispatch2._Uninitialized(arrowName, source);
		}

		@Override
		public RuleResult execute(VirtualFrame frame) {
			Object[] args = lhs.executeObjectArray(frame);
			return dispatcher.executeDispatch(frame, args);
		}

	}

}
