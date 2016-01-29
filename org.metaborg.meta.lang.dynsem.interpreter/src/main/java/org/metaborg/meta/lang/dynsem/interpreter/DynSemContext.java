package org.metaborg.meta.lang.dynsem.interpreter;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
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

	public Class<TermBuild> lookupTermBuildClass(String name, int arity) {
		throw new NotImplementedException();
	}

	public Class<MatchPattern> lookupMatchPatternClass(String name, int arity) {
		throw new NotImplementedException();
	}

}
