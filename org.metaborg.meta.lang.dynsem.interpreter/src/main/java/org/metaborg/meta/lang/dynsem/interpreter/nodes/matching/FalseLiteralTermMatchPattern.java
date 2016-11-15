package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class FalseLiteralTermMatchPattern extends LiteralMatchPattern {

	public FalseLiteralTermMatchPattern(SourceSection source) {
		super(source);
	}

	private final ConditionProfile profile = ConditionProfile.createCountingProfile();

	@Specialization
	public void doSuccess(boolean b) {
		if (profile.profile(b)) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

}