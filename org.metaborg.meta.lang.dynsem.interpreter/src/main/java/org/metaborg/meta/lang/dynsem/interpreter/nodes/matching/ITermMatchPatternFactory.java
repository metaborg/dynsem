package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.source.SourceSection;

public interface ITermMatchPatternFactory {

	default public MatchPattern apply(SourceSection source, MatchPattern... children) {
		throw new RuntimeException("Operation is not supported");
	}

	default public MatchPattern apply(SourceSection source, Object... objects) {
		throw new RuntimeException("Operation is not supported");
	}
}
