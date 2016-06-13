package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;

public interface ITerm {
	
	@Deprecated
	public Object[] allSubterms();
	
	public int size();

	public ITermInstanceChecker getCheck();
}
