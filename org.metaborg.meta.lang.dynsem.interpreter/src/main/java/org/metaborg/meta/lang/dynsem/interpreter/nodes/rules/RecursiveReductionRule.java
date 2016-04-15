package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class RecursiveReductionRule extends ReductionRule {

	public RecursiveReductionRule(SourceSection source, FrameDescriptor fd, String key, RuleInputsNode inputsNode,
			Premise[] premises, RuleTarget output) {
		super(source, fd, key, inputsNode, premises, output);
	}

	private final BranchProfile recurTaken = BranchProfile.create();

	@Override
	public RuleResult execute(VirtualFrame frame) {
		RuleResult result = null;
		boolean repeat = true;
		while (repeat) {
			try {
				result = super.execute(frame);
				repeat = false;
			} catch (RecurException recex) {
				recurTaken.enter();
				repeat = true;
			}
		}

		assert result != null;
		return result;
	}

	public static RecursiveReductionRule create(IStrategoAppl ruleT) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(ruleT, "RecRule", 3) : "Unexpected constructor " + ruleT.getConstructor();

		FrameDescriptor fd = createFrameDescriptor(ruleT);

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = Premise.create(Tools.applAt(premisesTerm, i), fd);
		}

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 3);
		IStrategoAppl lhsSourceTerm = Tools.applAt(relationT, 0);
		IStrategoAppl lhsLeftTerm = Tools.applAt(lhsSourceTerm, 0);
		IStrategoList lhsCompsTerm = Tools.listAt(lhsSourceTerm, 1);

		IStrategoAppl lhsConTerm = null;

		if (Tools.hasConstructor(lhsLeftTerm, "As", 2)) {
			lhsConTerm = Tools.applAt(lhsLeftTerm, 1);
		} else {
			lhsConTerm = lhsLeftTerm;
		}

		RuleTarget target = RuleTarget.create(Tools.applAt(relationT, 2), fd);

		return new RecursiveReductionRule(SourceSectionUtil.fromStrategoTerm(ruleT), fd, createRuleKey(relationT),
				RuleInputsNode.create(lhsConTerm, lhsCompsTerm, fd), premises, target);
	}

}
