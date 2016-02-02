package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.source.SourceSection;

public interface ITermMatchPatternFactory {

	public MatchPattern apply(SourceSection source, MatchPattern... children);
}
