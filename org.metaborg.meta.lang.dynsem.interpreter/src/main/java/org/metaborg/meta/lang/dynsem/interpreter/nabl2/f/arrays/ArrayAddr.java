package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.spoofax.interpreter.terms.IStrategoTerm;

public final class ArrayAddr extends Addr {

	private final Array arr;
	private final int idx;

	public ArrayAddr(Array arr, int idx) {
		this.arr = arr;
		this.idx = idx;
	}

	public Array arr() {
		return arr;
	}

	public int idx() {
		return idx;
	}

	@Override
	public int size() {
		return 2;
	}

	public Array get_1() {
		return arr();
	}

	public int get_2() {
		return idx();
	}

	@Override
	public ITermInstanceChecker getCheck() {
		return null;
	}

	@Override
	public boolean hasStrategoTerm() {
		return false;
	}

	@Override
	public IStrategoTerm getStrategoTerm() {
		return null;
	}

}
