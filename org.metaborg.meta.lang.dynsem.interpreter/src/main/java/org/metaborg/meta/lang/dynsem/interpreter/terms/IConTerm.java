package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;

public interface IConTerm extends ITerm {

	public String constructor();

	public int arity();

	public Object[] allSubterms();

	public ITermInstanceChecker getCheck();
}
