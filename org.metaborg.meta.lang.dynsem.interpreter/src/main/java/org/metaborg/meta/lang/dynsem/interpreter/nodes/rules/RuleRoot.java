package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;
import org.strategoxt.lang.Context;

import trans.pp_type_0_0;
import trans.rw_type_0_0;
import trans.trans;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class RuleRoot extends RootNode {

	@Child protected Rule rule;

	public RuleRoot(Rule rule, FrameDescriptor fd) {
		super(DynSemLanguage.class, rule.getSourceSection(), fd);
		this.rule = rule;
		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		return rule.execute(frame);
	}

	public Rule getRule() {
		return rule;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "RuleRoot: " + rule.getName() + "/" + rule.getConstructor() + "/" + rule.getArity();
	}

	public static RuleRoot create(IStrategoTerm ruleT) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.isTermAppl(ruleT);
		assert Tools.hasConstructor((IStrategoAppl) ruleT, "Rule", 3);

		FrameDescriptor fd = createFrameDescriptor(ruleT);

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = Premise.create(Tools.applAt(premisesTerm, i), fd);
		}

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 3);

		IStrategoAppl arrowTerm = Tools.applAt(relationT, 1);
		assert Tools.hasConstructor(arrowTerm, "NamedDynamicEmitted", 2);

		String name = Tools.stringAt(arrowTerm, 1).stringValue();

		IStrategoAppl lhsSourceTerm = Tools.applAt(relationT, 0);
		IStrategoAppl lhsLeftTerm = Tools.applAt(lhsSourceTerm, 0);
		IStrategoAppl lhsConTerm = null;

		// FIXME this should be done differently perhaps through desugaring of
		// the spec to bring the constructor name and arity outwards
		if (Tools.hasConstructor(lhsLeftTerm, "As", 2)) {
			lhsConTerm = Tools.applAt(lhsLeftTerm, 1);
		} else {
			lhsConTerm = lhsLeftTerm;
		}

		String constr = null;
		int arity;
		if (Tools.hasConstructor(lhsConTerm, "Con", 2)) {
			assert lhsConTerm != null && Tools.hasConstructor(lhsConTerm, "Con", 2);
			lhsConTerm = lhsLeftTerm;
			constr = Tools.stringAt(lhsConTerm, 0).stringValue();
			arity = Tools.listAt(lhsConTerm, 1).size();
		} else if (Tools.hasConstructor(lhsConTerm, "Cast", 2)) {
			IStrategoAppl tyTerm = Tools.applAt(lhsConTerm, 1);
			assert Tools.hasConstructor(tyTerm, "ListSort", 1);
			Context ctx = trans.init();
			constr = "_"
					+ Tools.asJavaString(pp_type_0_0.instance.invoke(ctx, rw_type_0_0.instance.invoke(ctx, tyTerm)));
			arity = 1;
		} else {
			throw new RuntimeException("Unsupported rule LHS: " + lhsLeftTerm);
		}

		IStrategoList lhsSemCompTerms = Tools.listAt(lhsSourceTerm, 1);
		MatchPattern[] lhsSemCompPatterns = new MatchPattern[lhsSemCompTerms.size()];
		for (int i = 0; i < lhsSemCompPatterns.length; i++) {
			lhsSemCompPatterns[i] = MatchPattern.create(Tools.applAt(lhsSemCompTerms, i), fd);
		}

		RuleTarget target = RuleTarget.create(Tools.applAt(relationT, 2), fd);

		return new RuleRoot(new DynSemRule(name, constr, arity, MatchPattern.create(lhsConTerm, fd),
				lhsSemCompPatterns, premises, target, SourceSectionUtil.fromStrategoTerm(ruleT)), fd);
	}

	private static FrameDescriptor createFrameDescriptor(IStrategoTerm t) {
		Set<String> vars = new HashSet<>();
		TermVisitor visitor = new TermVisitor() {

			@Override
			public void preVisit(IStrategoTerm t) {
				if (Tools.isTermAppl(t) && Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)) {
					vars.add(Tools.stringAt(t, 0).stringValue());
				}
			}
		};

		visitor.visit(t);
		FrameDescriptor fd = FrameDescriptor.create();
		for (String v : vars) {
			fd.addFrameSlot(v);
		}
		return fd;
	}
}
