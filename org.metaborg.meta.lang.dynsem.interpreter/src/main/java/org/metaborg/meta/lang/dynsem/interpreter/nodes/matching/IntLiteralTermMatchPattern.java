package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IntLiteralTermMatchPattern extends LiteralMatchPattern {

	protected final int lit;

	public IntLiteralTermMatchPattern(int lit, SourceSection source) {
		super(source);
		this.lit = lit;
	}

	private final ConditionProfile profile = ConditionProfile.createCountingProfile();

	@Specialization
	public void doSuccess(int i) {
		if (profile.profile(i != lit)) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

}