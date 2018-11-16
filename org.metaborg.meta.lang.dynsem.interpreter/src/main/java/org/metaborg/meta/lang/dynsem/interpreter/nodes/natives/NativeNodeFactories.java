package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.BuildNodeFactories;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchNodeFactories;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions.HandleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions.RaiseNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.BreakNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.ContinueNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.WhileNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

public final class NativeNodeFactories {
	private NativeNodeFactories() {

	}

	public static NativeExecutableNode create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor ruleFD,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		// WhileNode: Term * Term * Term * List(Term) * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "CountedWhileNode", 5)) {
			return createWhile(lang, t, ruleFD, termReg);
		}
		// BreakNode: Term * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "BreakNode", 2)) {
			return createBreak(lang, t, ruleFD, termReg);
		}
		// ContinueNode: Term * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "ContinueNode", 2)) {
			return createContinue(lang, t, ruleFD, termReg);
		}
		// Raise: List(Term) * Term -> NativeRule
		if (Tools.hasConstructor(t, "Raise", 2)) {
			return createRaise(lang, t, ruleFD, termReg);
		}
		// Handle: Term * Term -> NativeRule
		// Handle: Term * Term * Term -> NativeRule
		if (Tools.hasConstructor(t, "Handle", 2) || Tools.hasConstructor(t, "Handle", 3)) {
			return createHandle(lang, t, ruleFD, termReg);
		}

		throw new NotImplementedException("Unsupported dynsemrulenode: " + t);
	}

	public static NativeCallPremise createNativeCallPremise(DynSemLanguage lang, IStrategoAppl t,
			FrameDescriptor ruleFD, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		// NativeRelationPremise: NativeRule * Term * List(Term) -> Relation
		assert Tools.hasConstructor(t, "NativeRelationPremise", 3);
		NativeExecutableNode ruleNode = create(lang, Tools.applAt(t, 0), ruleFD, termReg);
		MatchPattern rhsNode = MatchNodeFactories.create(Tools.applAt(t, 1), ruleFD, termReg);

		IStrategoList rhsRwsT = Tools.listAt(t, 2);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchNodeFactories.create(Tools.applAt(rhsRwsT, i), ruleFD, termReg);
		}

		return NativeCallPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), ruleNode, rhsNode,
				rhsRwNodes);
	}

	public static HandleNode createHandle(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Handle", 2) || Tools.hasConstructor(t, "Handle", 3);
		// Handle: Term * Term * Term -> NativeRule
		TermBuild evalBuildNode = BuildNodeFactories.create(Tools.applAt(t, 0), fd, termReg);
		TermBuild catchBuildNode = BuildNodeFactories.create(Tools.applAt(t, 1), fd, termReg);
		TermBuild continueBuildNode = t.getConstructor().getArity() == 3
				? BuildNodeFactories.create(Tools.applAt(t, 2), fd, termReg)
				: null;

		return new HandleNode(SourceUtils.dynsemSourceSectionFromATerm(t), evalBuildNode, catchBuildNode,
				continueBuildNode);
	}

	public static RaiseNode createRaise(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Raise", 2);
		IStrategoList rwCompTerms = Tools.listAt(t, 0);
		TermBuild[] rwCompBuildNodes = new TermBuild[rwCompTerms.size()];
		for (int i = 0; i < rwCompBuildNodes.length; i++) {
			rwCompBuildNodes[i] = BuildNodeFactories.create(Tools.applAt(rwCompTerms, i), fd, termReg);
		}
		TermBuild thrownBuldNode = BuildNodeFactories.create(Tools.applAt(t, 1), fd, termReg);
		return new RaiseNode(SourceUtils.dynsemSourceSectionFromATerm(t), rwCompBuildNodes, thrownBuldNode);
	}

	public static BreakNode createBreak(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		// BreakNode: Term * List(Term) -> NativeRule
		assert Tools.hasConstructor(t, "BreakNode", 2);

		TermBuild valBuildNode = BuildNodeFactories.create(Tools.applAt(t, 0), fd, termReg);

		IStrategoList rwCompsT = Tools.listAt(t, 1);
		TermBuild[] rwCompBuilds = new TermBuild[rwCompsT.size()];
		for (int i = 0; i < rwCompBuilds.length; i++) {
			rwCompBuilds[i] = BuildNodeFactories.create(Tools.applAt(rwCompsT, i), fd, termReg);
		}

		return new BreakNode(SourceUtils.dynsemSourceSectionFromATerm(t), valBuildNode, rwCompBuilds);
	}

	public static ContinueNode createContinue(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		// ContinueNode: Term * List(Term) -> NativeRule
		assert Tools.hasConstructor(t, "ContinueNode", 2);

		TermBuild valBuildNode = BuildNodeFactories.create(Tools.applAt(t, 0), fd, termReg);

		IStrategoList rwCompsT = Tools.listAt(t, 1);
		TermBuild[] rwCompBuilds = new TermBuild[rwCompsT.size()];
		for (int i = 0; i < rwCompBuilds.length; i++) {
			rwCompBuilds[i] = BuildNodeFactories.create(Tools.applAt(rwCompsT, i), fd, termReg);
		}

		return new ContinueNode(SourceUtils.dynsemSourceSectionFromATerm(t), valBuildNode, rwCompBuilds);
	}

	public static WhileNode createWhile(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		// WhileNode: Int * Term * Term * Term * List(Term) -> NativeRule
		assert Tools.hasConstructor(t, "CountedWhileNode", 5);
		FrameSlot componentsFrameSlot = fd.findFrameSlot(WhileNode.genComponentsFrameSlotName(Tools.javaIntAt(t, 0)));
		FrameSlot resultFrameSlot = fd.findFrameSlot(WhileNode.genResultFrameSlotName(Tools.javaIntAt(t, 0)));
		TermBuild conditionBuildNode = BuildNodeFactories.create(Tools.applAt(t, 1), fd, termReg);
		TermBuild bodyBuildNode = BuildNodeFactories.create(Tools.applAt(t, 2), fd, termReg);
		TermBuild defaultValBuildNode = BuildNodeFactories.create(Tools.applAt(t, 3), fd, termReg);

		IStrategoList compsT = Tools.listAt(t, 4);
		TermBuild[] compBuilds = new TermBuild[compsT.size()];
		for (int i = 0; i < compBuilds.length; i++) {
			compBuilds[i] = BuildNodeFactories.create(Tools.applAt(compsT, i), fd, termReg);
		}

		// IStrategoList rwCompsT = Tools.listAt(t, 5);
		// TermBuild[] rwCompBuilds = new TermBuild[rwCompsT.size()];
		// for (int i = 0; i < rwCompBuilds.length; i++) {
		// rwCompBuilds[i] = TermBuild.create(Tools.applAt(rwCompsT, i), fd);
		// }

		return new WhileNode(SourceUtils.dynsemSourceSectionFromATerm(t), conditionBuildNode, bodyBuildNode,
				defaultValBuildNode, compBuilds, componentsFrameSlot, resultFrameSlot);
	}
}
