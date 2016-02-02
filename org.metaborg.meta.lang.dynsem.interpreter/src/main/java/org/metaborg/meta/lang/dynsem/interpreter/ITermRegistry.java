package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;

public interface ITermRegistry {
	public ITermBuildFactory getBuildFactory(String constr, int arity);

	public ITermMatchPatternFactory getMatcherFactory(String name, int arity);
}
