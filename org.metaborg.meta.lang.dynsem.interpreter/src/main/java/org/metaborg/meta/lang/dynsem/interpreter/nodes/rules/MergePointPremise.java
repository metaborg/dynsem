package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.ConditionProfile;

public class MergePointPremise extends Premise {

	@Child protected Premise condition;
	@Child protected Premise branch1;
	@Child protected Premise branch2;

	public MergePointPremise(Premise condition, Premise branch1,
			Premise branch2, SourceSection source) {
		super(source);
		this.condition = condition;
		this.branch1 = branch1;
		this.branch2 = branch2;
	}

	public static MergePointPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "MergePoint", 3);
		Premise condition = Premise.create(Tools.applAt(t, 0), fd);
		IStrategoList branch1Ts = Tools.listAt(Tools.applAt(t, 1), 0);
		Premise[] branch1Premises = new Premise[branch1Ts.size()];
		for (int i = 0; i < branch1Premises.length; i++) {
			branch1Premises[i] = Premise.create(Tools.applAt(branch1Ts, i), fd);
		}
		Premise branch1 = new PremisesSequence(branch1Premises,
				SourceSectionUtil.fromStrategoTerm(Tools.termAt(t, 1)));

		IStrategoList branch2Ts = Tools.listAt(Tools.applAt(t, 2), 0);
		Premise[] branch2Premises = new Premise[branch2Ts.size()];
		for (int i = 0; i < branch2Premises.length; i++) {
			branch2Premises[i] = Premise.create(Tools.applAt(branch2Ts, i), fd);
		}
		Premise branch2 = new PremisesSequence(branch2Premises,
				SourceSectionUtil.fromStrategoTerm(Tools.termAt(t, 2)));

		return new MergePointPremise(condition, branch1, branch2,
				SourceSectionUtil.fromStrategoTerm(t));
	}

	private final ConditionProfile conditionProfile = ConditionProfile
			.createCountingProfile();

	@Override
	public void execute(VirtualFrame frame) {
		if (conditionProfile.profile(evaluateCondition(frame))) {
			branch1.execute(frame);
		} else {
			branch2.execute(frame);
		}
	}

	private boolean evaluateCondition(VirtualFrame frame) {
		try {
			condition.execute(frame);
			return true;
		} catch (PremiseFailure f) {
			return false;
		}
	}

	@Override
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

}
