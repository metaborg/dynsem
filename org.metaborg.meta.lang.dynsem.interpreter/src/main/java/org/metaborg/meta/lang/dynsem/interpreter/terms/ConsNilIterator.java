package org.metaborg.meta.lang.dynsem.interpreter.terms;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConsNilIterator<T> implements Iterator<T> {

	private IListTerm<T> current;

	public ConsNilIterator(IListTerm<T> l) {
		this.current = l;
	}

	@Override
	public boolean hasNext() {
		return current.elem() != null;
	}

	@Override
	public T next() {
		if (current.elem() == null) {
			throw new NoSuchElementException();
		}

		current = current.tail();

		T elem = current.elem();
		return elem;
	}

}
