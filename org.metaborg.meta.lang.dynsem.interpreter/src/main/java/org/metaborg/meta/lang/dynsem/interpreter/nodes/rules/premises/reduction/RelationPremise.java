package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.RelationDispatch;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
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
public class RelationPremise extends Premise {

	@Child protected RelationDispatch dispatchNode;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	public RelationPremise(RelationDispatch dispatch, MatchPattern rhsNode, MatchPattern[] rhsComponentNodes,
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
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			if (!rhsRwNodes[i].execute(components[i], frame)) {
				return false;
			}
		}
		return true;
	}

	public static RelationPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Relation", 4);

		IStrategoAppl targetT = Tools.applAt(t, 3);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchPattern.create(Tools.applAt(targetT, 0), fd);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.createFromLabelComp(Tools.applAt(rhsRwsT, i), fd);
		}

		return new RelationPremise(RelationDispatch.create(Tools.applAt(t, 0), Tools.applAt(t, 1), Tools.applAt(t, 2),
				fd), rhsNode, rhsRwNodes, SourceSectionUtil.fromStrategoTerm(t));
	}
}
