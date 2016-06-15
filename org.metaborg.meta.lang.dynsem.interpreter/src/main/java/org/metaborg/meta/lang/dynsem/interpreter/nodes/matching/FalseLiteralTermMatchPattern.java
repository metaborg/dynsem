package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class FalseLiteralTermMatchPattern extends LiteralMatchPattern {

	public FalseLiteralTermMatchPattern(SourceSection source) {
		super(source);
	}

	@Specialization(guards = "!b")
	public void doSuccess(boolean b) {

	}

	@Specialization
	public void doFailure(boolean b) {
		throw PatternMatchFailure.INSTANCE;
	}

}