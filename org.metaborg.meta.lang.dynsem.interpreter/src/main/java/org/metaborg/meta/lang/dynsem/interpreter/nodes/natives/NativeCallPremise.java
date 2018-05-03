package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NativeCallPremise extends Premise {
	@Child private NativeExecutableNode execNode;

	@Child private MatchPattern rhsNode;

	@Children private final MatchPattern[] rhsRwNodes;

	public NativeCallPremise(SourceSection source, NativeExecutableNode execNode, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes) {
		super(source);
		this.execNode = execNode;
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
	}

	@Specialization
	@ExplodeLoop
	public void doExecute(VirtualFrame frame) {
		final RuleResult res = execNode.execute(frame);

		rhsNode.executeMatch(frame, res.result);

		// evaluate the RHS component pattern matches
		final Object[] components = res.components;
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i].executeMatch(frame, InterpreterUtils.getComponent(getContext(), components, i, this));
		}
	}

	public static NativeCallPremise create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor ruleFD) {
		CompilerAsserts.neverPartOfCompilation();
		// NativeRelationPremise: NativeRule * Term * List(Term) -> Relation
		assert Tools.hasConstructor(t, "NativeRelationPremise", 3);
		NativeExecutableNode ruleNode = NativeExecutableNode.create(lang, Tools.applAt(t, 0), ruleFD);
		MatchPattern rhsNode = MatchPattern.create(Tools.applAt(t, 1), ruleFD);

		IStrategoList rhsRwsT = Tools.listAt(t, 2);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.create(Tools.applAt(rhsRwsT, i), ruleFD);
		}

		return NativeCallPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), ruleNode, rhsNode,
				rhsRwNodes);
	}

}
