package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.spoofax.terms.util.NotImplementedException;

public class DynSemContext {

	private final ITermRegistry termRegistry;

	public DynSemContext(ITermRegistry termRegistry) {
		this.termRegistry = termRegistry;
	}

	public Rule lookupRule(String name, int arity) {
		throw new NotImplementedException();
	}

	public ITermBuildFactory lookupTermBuilder(String name, int arity) {
		return termRegistry.lookupBuildFactory(name, arity);
	}

	public ITermMatchPatternFactory lookupMatchPattern(String name, int arity) {
		return termRegistry.lookupMatchFactory(name, arity);
	}

}
