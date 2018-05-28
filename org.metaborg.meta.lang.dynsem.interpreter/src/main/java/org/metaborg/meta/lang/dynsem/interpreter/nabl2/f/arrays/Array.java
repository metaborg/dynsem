package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays;

import java.util.Arrays;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;

public final class Array {

	private final Object[] data;

	public Array(int length, Object fillValue) {
		this.data = new Object[length];
		Arrays.fill(this.data, fillValue);
	}

	public Addr lookup(int idx) {
		return new ArrayAddr(this, idx);
	}

	public Object get(int idx) {
		return data[idx];
	}

	public Object set(int idx, Object val) {
		data[idx] = val;
		return val;
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj);
	}

}
