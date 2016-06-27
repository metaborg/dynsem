package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;

public interface ITerm {

	public int size();

	public ITermInstanceChecker getCheck();
}
