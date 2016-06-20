package org.metaborg.meta.lang.dynsem.interpreter.terms;

import java.util.Iterator;

public interface IListTerm<T> extends ITerm {

	public T head();

	public IListTerm<T> tail();

	public IListTerm<T> add(T elem);
	
	public IListTerm<T> addAll(T[] elems);
	
	public Iterator<T> iterator();

}
