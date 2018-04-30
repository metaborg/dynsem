package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

public final class Nil extends ConsNilList {

	public Nil(String sort, IStrategoTerm aterm) {
		super(sort, aterm);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object head() {
		throw new IllegalStateException("No head of Nil list");
	}

	@Override
	public IListTerm tail() {
		throw new IllegalStateException("No tail of Nil list");
	}

	@Override
	public String toString() {
		return "N()";
	}

}
