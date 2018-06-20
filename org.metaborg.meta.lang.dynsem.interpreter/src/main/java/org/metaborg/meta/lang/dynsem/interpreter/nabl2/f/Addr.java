package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

public abstract class Addr implements IApplTerm {

	@Override
	public Class<?> getSortClass() {
		return Addr.class;
	}

}
