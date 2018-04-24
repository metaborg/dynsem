package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class AsMatch extends MatchPattern {

	@Child private VarBind varNode;
	@Child private MatchPattern patternNode;

	public AsMatch(SourceSection source, VarBind varNode, MatchPattern patternNode) {
		super(source);
		this.varNode = varNode;
		this.patternNode = patternNode;
	}


	@Specialization
	public boolean executeMatch(VirtualFrame frame, Object t,
			@Cached("createBinaryProfile()") ConditionProfile profile1,
			@Cached("createBinaryProfile()") ConditionProfile profile2) {
		return profile1.profile(patternNode.executeMatch(frame, t)) && profile2.profile(varNode.executeMatch(frame, t));
	}

	public static AsMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "As", 2);
		return AsMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				VarBind.create(Tools.applAt(t, 0), fd),
				MatchPattern.create(Tools.applAt(t, 1), fd));

	}
}
