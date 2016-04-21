package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;

import trans.pp_type_0_0;
import trans.trans;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RelationDispatch extends DynSemNode {

	public RelationDispatch(SourceSection source) {
		super(source);
	}

	public static RelationDispatch create(IStrategoAppl source, IStrategoAppl arrow, FrameDescriptor fd) {

		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 2);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		IStrategoAppl lhsT = Tools.applAt(source, 0);
		IStrategoConstructor lhsC = lhsT.getConstructor();
		if (lhsC.getName().equals("Con") && lhsC.getArity() == 2) {
			// static dispatch
			return new MetafunctionDispatch(Tools.stringAt(lhsT, 0).stringValue(), Tools.listAt(lhsT, 1).size(),
					arrowName, SourceSectionUtil.fromStrategoTerm(source));
		} else if (lhsC.getName().equals("ListSource") && lhsC.getArity() == 2) {
			String key = "_" + Tools.asJavaString(pp_type_0_0.instance.invoke(trans.init(), Tools.termAt(lhsT, 1)));
			return new ListRelationDispatch(key, arrowName, SourceSectionUtil.fromStrategoTerm(lhsT));
		} else {
			// dynamic dispatch
			return new DynamicRelationDispatch._Uninitialized(arrowName, SourceSectionUtil.fromStrategoTerm(source));
		}
	}

	public abstract RuleResult execute(VirtualFrame frame, Object[] args);

	public static class MetafunctionDispatch extends RelationDispatch {

		private final String conName;
		private final int arity;
		private final String arrowName;

		public MetafunctionDispatch(String conName, int arity, String arrowName, SourceSection source) {
			super(source);
			this.conName = conName;
			this.arrowName = arrowName;
			this.arity = arity;
		}

		@Override
		public RuleResult execute(VirtualFrame frame, Object[] args) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			RuleRoot rr = getContext().getRuleRegistry().lookupRule(arrowName, conName, arity);
			return replace(
					new InlinedRelationDispatch(NodeUtil.cloneNode(rr.getRule()), rr.getFrameDescriptor(),
							getSourceSection())).execute(frame, args);
		}

	}

	public static class ListRelationDispatch extends RelationDispatch {

		private final String arrowName;
		private final String ruleKey;

		public ListRelationDispatch(String ruleKey, String arrowName, SourceSection source) {
			super(source);
			this.arrowName = arrowName;
			this.ruleKey = ruleKey;
		}

		@Override
		public RuleResult execute(VirtualFrame frame, Object[] args) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			RuleRoot rr = getContext().getRuleRegistry().lookupRule(arrowName, ruleKey, 1);
			return replace(
					new InlinedRelationDispatch(NodeUtil.cloneNode(rr.getRule()), rr.getFrameDescriptor(),
							getSourceSection())).execute(frame, args);
		}

	}

	public static class InlinedRelationDispatch extends RelationDispatch {

		@Child protected Rule rule;
		private final FrameDescriptor fd;

		public InlinedRelationDispatch(Rule rule, FrameDescriptor fd, SourceSection source) {
			super(source);
			this.rule = rule;
			this.fd = fd;
		}

		public RuleResult execute(VirtualFrame frame, Object[] args) {
			return rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
		}

	}

}
