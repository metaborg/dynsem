package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

@Deprecated
public class MergePointPremise extends Premise {

	@Child protected Premise condition;
	@Children protected final Premise[] branch1;
	@Children protected final Premise[] branch2;

	public MergePointPremise(Premise condition, Premise[] branch1, Premise[] branch2, SourceSection source) {
		super(source);
		this.condition = condition;
		this.branch1 = branch1;
		this.branch2 = branch2;
	}

	public static MergePointPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MergePoint", 3);
		Premise condition = Premise.create(Tools.applAt(t, 0), fd);
		IStrategoList branch1Ts = Tools.listAt(Tools.applAt(t, 1), 0);
		Premise[] branch1 = new Premise[branch1Ts.size()];
		for (int i = 0; i < branch1.length; i++) {
			branch1[i] = Premise.create(Tools.applAt(branch1Ts, i), fd);
		}

		IStrategoList branch2Ts = Tools.listAt(Tools.applAt(t, 2), 0);
		Premise[] branch2 = new Premise[branch2Ts.size()];
		for (int i = 0; i < branch2.length; i++) {
			branch2[i] = Premise.create(Tools.applAt(branch2Ts, i), fd);
		}

		return new MergePointPremise(condition, branch1, branch2, SourceSectionUtil.fromStrategoTerm(t));
	}

	private final BranchProfile alternativeTaken = BranchProfile.create();

	@Override
	public void execute(VirtualFrame frame) {
		try {
			// execute the condition
			condition.execute(frame);
			execBranch1(frame);
		} catch (PatternMatchFailure pmfx) {
			// the condition has failed due to a failed pattern match so execute the alternative
			alternativeTaken.enter();
			execBranch2(frame);
		}
	}

	@ExplodeLoop
	private void execBranch1(VirtualFrame frame) {
		for (int i = 0; i < branch1.length; i++) {
			branch1[i].execute(frame);
		}
	}

	@ExplodeLoop
	private void execBranch2(VirtualFrame frame) {
		for (int i = 0; i < branch2.length; i++) {
			branch2[i].execute(frame);
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

}
