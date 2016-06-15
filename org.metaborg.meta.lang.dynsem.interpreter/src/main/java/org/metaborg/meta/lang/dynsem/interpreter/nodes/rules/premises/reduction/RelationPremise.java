package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import java.util.Objects;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.utils.ComponentUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

/**
 * {@link RelationPremise} represents and specifies evaluation logic for a reduction premise, i.e. a premise which
 * applies a rule to a term.
 * 
 * @author vladvergu
 *
 */
public class RelationPremise extends Premise {

	@Child protected RelationInvocationNode relationLhs;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	public RelationPremise(RelationPremiseInputBuilder inputBuilderNode, RelationDispatch dispatchNode,
			MatchPattern rhsNode, MatchPattern[] rhsComponentNodes, SourceSection source) {
		super(source);
		this.relationLhs = new RelationInvocationNode(inputBuilderNode, dispatchNode, source);
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
	}

	private final BranchProfile rhsMatchFailure = BranchProfile.create();
	private final BranchProfile rhsCompFailure = BranchProfile.create();

	@Override
	public void execute(VirtualFrame frame) {

		RuleResult ruleRes = relationLhs.execute(frame);

		if (!rhsNode.execute(ruleRes.result, frame)) {
			rhsMatchFailure.enter();
			throw PremiseFailure.INSTANCE;
		}
		if (!evalRhsComponents(ruleRes.components, frame)) {
			rhsCompFailure.enter();
			throw PremiseFailure.INSTANCE;
		}

	}

	@ExplodeLoop
	private boolean evalRhsComponents(Object[] components, VirtualFrame frame) {
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			if (!rhsRwNodes[i].execute(ComponentUtils.getComponent(components, i), frame)) {
				return false;
			}
		}
		return true;
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
		;
		return new RelationPremise(RelationPremiseInputBuilder.create(Tools.applAt(t, 0), fd),
				RelationDispatch.create(Tools.applAt(t, 0), Tools.applAt(t, 1), fd), rhsNode, rhsRwNodes,
				SourceSectionUtil.fromStrategoTerm(t));
	}
}
