package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TrueLiteralTermMatchPattern extends LiteralMatchPattern {

	public TrueLiteralTermMatchPattern(SourceSection source) {
		super(source);
	}

	private final ConditionProfile profile = ConditionProfile.createCountingProfile();

	@Specialization
	public boolean doSuccess(boolean b) {
		return profile.profile(b);
	}

}