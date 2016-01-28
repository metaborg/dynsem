package org.metaborg.meta.dynsem.interpreter;

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

}
