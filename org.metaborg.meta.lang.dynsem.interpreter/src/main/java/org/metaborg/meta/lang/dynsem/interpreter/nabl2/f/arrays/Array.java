package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;
import org.metaborg.meta.lang.dynsem.interpreter.terms.shared.ValSort;

public abstract class Array {

	public abstract Addr lookup(int idx);

	public abstract ValSort get(int idx);

	public abstract ValSort set(int idx, ValSort val);

	@Override
	public boolean equals(Object obj) {
		return (this == obj);
	}

}
