package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.BuildNodeFactories;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchNodeFactories;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeNodeFactories;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;

public final class PremiseFactories {
	private PremiseFactories() {

	}

	@TruffleBoundary
	public static Premise create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "CaseMatch", 2)) {
			return createCase(lang, t, fd, termReg);
		}
		IStrategoAppl premT = Tools.applAt(t, 0);
		if (Tools.hasConstructor(premT, "Relation", 3)) {
			return createRelation(premT, fd, termReg);
		}
		if (Tools.hasConstructor(premT, "Match", 2)) {
			return createMatch(premT, fd, termReg);
		}
		if (Tools.hasConstructor(premT, "NativeRelationPremise", 3)) {
			return NativeNodeFactories.createNativeCallPremise(lang, premT, fd, termReg);
		}
		if (Tools.hasConstructor(premT, "TermEq", 2)) {
			return createTermEq(premT, fd, termReg);
		}
		if (Tools.hasConstructor(premT, "Fails", 1)) {
			return createFails(lang, premT, fd, termReg);
		}

		throw new NotImplementedException("Unsupported premise: " + t);
	}

	public static CaseMatchPremise createCase(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "CaseMatch", 2);
		TermBuild tb = BuildNodeFactories.create(Tools.applAt(t, 0), fd, termReg);

		return CaseMatchPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				Case2.create(lang, Tools.listAt(t, 1), fd, termReg), tb);
	}

	@Deprecated
	public static FailsPremise createFails(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "Fails", 1);
		return new FailsPremise(SourceUtils.dynsemSourceSectionFromATerm(t),
				create(lang, Tools.termAt(t, 0), fd, termReg));
	}

	public static MatchPremise createMatch(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Match", 2);
		TermBuild lhs = BuildNodeFactories.create(Tools.applAt(t, 0), fd, termReg);
		MatchPattern rhs = MatchNodeFactories.create(Tools.applAt(t, 1), fd, termReg);
		return new MatchPremise(lhs, rhs, SourceUtils.dynsemSourceSectionFromATerm(t));
		// return MatchPremiseFactory.NonElidableMatchPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
		// rhs, lhs);
	}

	public static RelationPremise createRelation(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Relation", 3);

		IStrategoAppl targetT = Tools.applAt(t, 2);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchNodeFactories.create(Tools.applAt(targetT, 0), fd, termReg);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchNodeFactories.createFromLabelComp(Tools.applAt(rhsRwsT, i), fd, termReg);
		}
		IStrategoAppl arrow = Tools.applAt(t, 1);
		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 3);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();
		IStrategoAppl source = Tools.applAt(t, 0);
		assert Tools.hasConstructor(source, "Source", 2);

		TermBuild lhsNode = BuildNodeFactories.create(Tools.applAt(source, 0), fd, termReg);

		IStrategoList rws = Tools.listAt(source, 1);
		TermBuild[] rwNodes = new TermBuild[rws.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = BuildNodeFactories.createFromLabelComp(Tools.applAt(rws, i), fd, termReg);
		}

		return RelationPremiseNodeGen.create(arrowName, lhsNode, rwNodes, rhsNode, rhsRwNodes,
				SourceUtils.dynsemSourceSectionFromATerm(t));

	}

	public static TermEqPremise createTermEq(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TermEq", 2);
		TermBuild lhs = BuildNodeFactories.create(Tools.applAt(t, 0), fd, termReg);
		TermBuild rhs = BuildNodeFactories.create(Tools.applAt(t, 1), fd, termReg);
		return TermEqPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), lhs, rhs);
	}
}
