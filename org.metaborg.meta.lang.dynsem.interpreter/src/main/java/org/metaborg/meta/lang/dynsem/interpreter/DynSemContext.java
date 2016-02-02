package org.metaborg.meta.lang.dynsem.interpreter;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.util.NotImplementedException;

public class DynSemContext {

	private ITermFactory termfactory;

	public DynSemContext() {
		this.termfactory = new TermFactory();
	}

	public ITermFactory getTermFactory() {
		return termfactory;
	}

	public Rule lookupRule(String name, int arity) {
		throw new NotImplementedException();
	}

	public Class<ITerm> lookupTermClass(String name, int arity) {
		throw new NotImplementedException();
	}

	public ITermBuildFactory lookupTermBuilder(
			String name, int arity) {
		throw new NotImplementedException();
	}

	public ITermMatchPatternFactory lookupMatchPattern(
			String name, int arity) {
		throw new NotImplementedException();
	}

}
