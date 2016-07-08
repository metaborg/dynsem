package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IntLiteralTermMatchPattern extends LiteralMatchPattern {

	protected final int lit;

	public IntLiteralTermMatchPattern(int lit, SourceSection source) {
		super(source);
		this.lit = lit;
	}

	@Specialization(guards = "i == lit")
	public void doSuccess(int i) {

	}

	@Specialization
	public void doFailure(int i) {
		throw PatternMatchFailure.INSTANCE;
	}

}