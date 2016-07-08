package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class StringLiteralTermMatchPattern extends LiteralMatchPattern {

	protected final String lit;

	public StringLiteralTermMatchPattern(String lit, SourceSection source) {
		super(source);
		this.lit = lit;
	}

	@Specialization(guards = "s == cachedS")
	public void doCachedString(String s, @Cached("s") String cachedS, @Cached("isStringEq(cachedS)") boolean isEq) {
		if (!isEq) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

	@Specialization(contains = "doCachedString")
	public void doUncachedString(String s) {
		if (!isStringEq(s)) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

	@TruffleBoundary
	protected final boolean isStringEq(String s) {
		return lit.equals(s);
	}

}