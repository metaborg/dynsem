package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;

public interface IConTerm extends ITerm {

	@Deprecated
	public String constructor();

	@Deprecated
	public int arity();

	@Deprecated
	public Object[] allSubterms();

	public ITermInstanceChecker getCheck();
}
