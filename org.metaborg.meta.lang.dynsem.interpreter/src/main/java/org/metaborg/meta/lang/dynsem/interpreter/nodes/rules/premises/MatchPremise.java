package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class MatchPremise extends Premise {

	@Child protected TermBuild term;
	@Child protected MatchPattern pat;

	public MatchPremise(TermBuild term, MatchPattern pattern, SourceSection source) {
		super(source);
		this.term = term;
		this.pat = pattern;
	}

	private final BranchProfile matchFailProfile = BranchProfile.create();

	@Override
	public void execute(VirtualFrame frame) {
		Object res = term.executeGeneric(frame);

		boolean matchsuccess = pat.execute(res, frame);

		if (!matchsuccess) {
			matchFailProfile.enter();
			throw new PremiseFailure();
		}
	}

	public static MatchPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Match", 2);
		TermBuild lhs = TermBuild.create(Tools.applAt(t, 0), fd);
		MatchPattern rhs = MatchPattern.create(Tools.applAt(t, 1), fd);
		return new MatchPremise(lhs, rhs, SourceSectionUtil.fromStrategoTerm(t));
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}
}
