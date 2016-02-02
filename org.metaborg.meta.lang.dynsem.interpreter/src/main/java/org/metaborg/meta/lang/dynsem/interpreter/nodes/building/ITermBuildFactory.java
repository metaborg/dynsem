package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.source.SourceSection;

public interface ITermBuildFactory {

	public TermBuild apply(SourceSection source, TermBuild... children);
}
