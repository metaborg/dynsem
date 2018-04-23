package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public class AsMatch extends MatchPattern {

	@Child private VarBind varNode;
	@Child private MatchPattern patternNode;

	public AsMatch(SourceSection source, VarBind varNode, MatchPattern patternNode) {
		super(source);
		this.varNode = varNode;
		this.patternNode = patternNode;
	}

	private final ConditionProfile c1Profile = ConditionProfile.createCountingProfile();
	private final ConditionProfile c2Profile = ConditionProfile.createCountingProfile();

	@Override
	public boolean executeMatch(VirtualFrame frame, Object t) {
		return c1Profile.profile(patternNode.executeMatch(frame, t))
				&& c2Profile.profile(varNode.executeMatch(frame, t));
	}

	public static AsMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "As", 2);
		return new AsMatch(SourceUtils.dynsemSourceSectionFromATerm(t), VarBind.create(Tools.applAt(t, 0), fd),
				MatchPattern.create(Tools.applAt(t, 1), fd));

	}
}
