package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.source.SourceSection;

public interface ITermBuildFactory {

	default public TermBuild apply(SourceSection source, TermBuild... children) {
		throw new RuntimeException("Operation is not supported");
	}

	default public TermBuild apply(SourceSection source, Object... objects) {
		throw new RuntimeException("Operation is not supported");
	}

}
