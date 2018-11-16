package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.source.SourceSection;

public abstract class LiteralMatchPattern extends MatchPattern {

	public LiteralMatchPattern(SourceSection source) {
		super(source);
	}

}
