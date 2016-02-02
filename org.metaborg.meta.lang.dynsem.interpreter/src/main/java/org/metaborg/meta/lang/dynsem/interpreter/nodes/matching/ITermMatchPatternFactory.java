package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.source.SourceSection;

public interface ITermMatchPatternFactory<T extends MatchPattern> {

	public T apply(SourceSection source, MatchPattern... children);
}
