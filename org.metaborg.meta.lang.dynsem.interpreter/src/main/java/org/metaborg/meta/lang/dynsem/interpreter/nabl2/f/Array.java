package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.spoofax.interpreter.terms.IStrategoTerm;

public abstract class Array {

	public Array() {
		// TODO Auto-generated constructor stub
	}

	public static Array create(IStrategoTerm t) {
		throw new RuntimeException("Not implemented");
	}

	// lookup: Int -> Addr
	public abstract Addr lookup(int idx);

	// get: Int -> Val
	public abstract Object get(int idx);

	// set: Int * Val -> Val
	public abstract Object set(int idx, Object val);

	// equals: Array -> Bool
	@Override
	public abstract boolean equals(Object o);

}
