package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class StringLiteralTermMatchPattern extends LiteralMatchPattern {

	protected final String lit;

	public StringLiteralTermMatchPattern(String lit, SourceSection source) {
		super(source);
		this.lit = lit;
	}

	@Specialization(guards = "s == cachedS")
	public boolean doCachedString(String s, @Cached("s") String cachedS, @Cached("isStringEq(cachedS)") boolean isEq) {
		return isEq;
	}

	private final ConditionProfile eqProfile = ConditionProfile.createCountingProfile();

	@Specialization(replaces = "doCachedString")
	public boolean doUncachedString(String s) {
		return eqProfile.profile(isStringEq(s));
	}

	@TruffleBoundary
	protected final boolean isStringEq(String s) {
		return lit.equals(s);
	}

}