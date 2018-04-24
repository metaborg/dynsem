package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TrueLiteralTermMatchPattern extends LiteralMatchPattern {

	public TrueLiteralTermMatchPattern(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean doSuccess(boolean b) {
		return b;
	}

}