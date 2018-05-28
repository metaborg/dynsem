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
		return current.size() > 0;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		T elem = current.elem();
		current = current.tail();
		return elem;
	}

}
