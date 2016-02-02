package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.source.SourceSection;

public interface ITermBuildFactory<T extends TermBuild> {

	public T apply(SourceSection source, TermBuild... children);
}
