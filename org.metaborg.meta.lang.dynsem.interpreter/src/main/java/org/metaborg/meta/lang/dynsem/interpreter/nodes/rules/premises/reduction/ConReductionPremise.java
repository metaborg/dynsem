package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ConBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.ReductionDispatch;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

/**
 * {@link ConReductionPremise} represents and specifies evaluation logic for a
 * reduction premise, i.e. a premise which applies a rule to a term.
 * 
 * @author vladvergu
 *
 */
public class ConReductionPremise extends Premise {

	@Child protected ReductionDispatch dispatchNode;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	public static ConReductionPremise createDynamicDispatch(
			TermBuild[] roNodes, TermBuild lhsNode, String arrowName,
			TermBuild[] rwNodes, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes, SourceSection source) {
		PremiseLhs lhs = new PremiseLhs(lhsNode, roNodes, rwNodes, source);
		ReductionDispatch dispatch = new ReductionDispatch.DynamicReductionDispatch(
				lhs, arrowName, source);
		return new ConReductionPremise(dispatch, rhsNode, rhsComponentNodes,
				source);
	}

	public static ConReductionPremise createStaticDispatch(TermBuild[] roNodes,
			TermBuild lhsNode, String arrowName, String conName, int arity,
			TermBuild[] rwNodes, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes, SourceSection source) {
		PremiseLhs lhs = new PremiseLhs(lhsNode, roNodes, rwNodes, source);
		ReductionDispatch dispatch = new ReductionDispatch.InlineableReductionDispatch(
				conName, arity, arrowName, lhs, source);
		return new ConReductionPremise(dispatch, rhsNode, rhsComponentNodes,
				source);
	}

	public ConReductionPremise(ReductionDispatch dispatch,
			MatchPattern rhsNode, MatchPattern[] rhsComponentNodes,
			SourceSection source) {
		super(source);
		this.dispatchNode = dispatch;
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
	}

	@Override
	public void execute(VirtualFrame frame) {

		RuleResult ruleRes = dispatchNode.execute(frame);

		if (!rhsNode.execute(ruleRes.result, frame)) {
			throw new PremiseFailure();
		}
		if (!evalRhsComponents(ruleRes.components, frame)) {
			throw new PremiseFailure();
		}

	}

	@ExplodeLoop
	private boolean evalRhsComponents(Object[] components, VirtualFrame frame) {
		CompilerAsserts.compilationConstant(components.length);
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			if (!rhsRwNodes[i].execute(components[i], frame)) {
				return false;
			}
		}
		return true;
	}

	public static ConReductionPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Relation", 4);

		IStrategoList rosT = Tools.listAt(Tools.applAt(t, 0), 0);
		TermBuild[] roNodes = new TermBuild[rosT.getSubtermCount()];
		for (int i = 0; i < roNodes.length; i++) {
			roNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rosT, i),
					fd);
		}

		IStrategoAppl arrowTerm = Tools.applAt(t, 2);
		assert Tools.hasConstructor(arrowTerm, "NamedDynamicEmitted", 2);

		String arrowName = Tools.stringAt(arrowTerm, 1).stringValue();

		IStrategoAppl sourceT = Tools.applAt(t, 1);
		assert Tools.hasConstructor(sourceT, "Source", 2);
		TermBuild lhsNode = TermBuild.create(Tools.applAt(sourceT, 0), fd);

		IStrategoList rwsT = Tools.listAt(sourceT, 1);
		TermBuild[] rwNodes = new TermBuild[rwsT.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rwsT, i),
					fd);
		}

		IStrategoAppl targetT = Tools.applAt(t, 3);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchPattern
				.create(Tools.applAt(targetT, 0), fd);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.createFromLabelComp(
					Tools.applAt(rhsRwsT, i), fd);
		}

		if (lhsNode instanceof ConBuild) {
			IStrategoAppl conBuildTerm = Tools.applAt(sourceT, 0);
			String conName = Tools.stringAt(conBuildTerm, 0).stringValue();
			int arity = Tools.listAt(conBuildTerm, 1).size();
			return ConReductionPremise.createStaticDispatch(roNodes, lhsNode,
					arrowName, conName, arity, rwNodes, rhsNode, rhsRwNodes,
					SourceSectionUtil.fromStrategoTerm(t));
		} else {
			return ConReductionPremise.createDynamicDispatch(roNodes, lhsNode,
					arrowName, rwNodes, rhsNode, rhsRwNodes,
					SourceSectionUtil.fromStrategoTerm(t));
		}
	}
}
