package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;

public class ArrayAddr implements Addr {

	private final Array arr;
	private final int idx;

	public ArrayAddr(Array arr, int idx) {
		this.arr = arr;
		this.idx = idx;
	}

}
