package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
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

/**
 * {@link RelationPremise} represents and specifies evaluation logic for a reduction premise, i.e. a premise which
 * applies a rule to a term.
 * 
 * @author vladvergu
 *
 */
public abstract class RelationPremise extends Premise {

	@Children protected final TermBuild[] lhsCompNodes;
	@Child protected RuleInvokeNode relationLhs;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	public RelationPremise(String arrowName, TermBuild termNode, TermBuild[] componentNodes, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes, SourceSection source) {
		super(source);
		this.lhsCompNodes = componentNodes;
		this.relationLhs = RuleInvokeNodeGen.create(source, arrowName, termNode);
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
	}

	@Specialization
	@ExplodeLoop
	public void executeWithProfile(VirtualFrame frame) {
		CompilerAsserts.compilationConstant(lhsCompNodes.length);
		Object[] args = new Object[lhsCompNodes.length + 1];

		for (int i = 0; i < lhsCompNodes.length; i++) {
			args[i + 1] = lhsCompNodes[i].executeGeneric(frame);
		}

		// execute the reduction
		final RuleResult res = relationLhs.execute(frame, args);

		// evaluate the RHS pattern match
		rhsNode.executeMatch(frame, res.result);

		// evaluate the RHS component pattern matches
		final Object[] components = res.components;
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i].executeMatch(frame, InterpreterUtils.getComponent(getContext(), components, i, this));
		}

	}

	public static RelationPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Relation", 3);

		IStrategoAppl targetT = Tools.applAt(t, 2);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchPattern.create(Tools.applAt(targetT, 0), fd);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.createFromLabelComp(Tools.applAt(rhsRwsT, i), fd);
		}
		IStrategoAppl arrow = Tools.applAt(t, 1);
		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 3);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();
		IStrategoAppl source = Tools.applAt(t, 0);
		assert Tools.hasConstructor(source, "Source", 2);

		TermBuild lhsNode = TermBuild.create(Tools.applAt(source, 0), fd);

		IStrategoList rws = Tools.listAt(source, 1);
		TermBuild[] rwNodes = new TermBuild[rws.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rws, i), fd);
		}

		return RelationPremiseNodeGen.create(arrowName, lhsNode, rwNodes, rhsNode, rhsRwNodes,
				SourceUtils.dynsemSourceSectionFromATerm(t));

	}
}
