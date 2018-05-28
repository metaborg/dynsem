package org.metaborg.meta.lang.dynsem.interpreter.terms;

/**
 * Interface for all list terms.
 * 
 * @author vladvergu
 *
 * @param <T>
 *            type of the elements in this list
 */
public interface IListTerm<T> extends ITerm, Iterable<T> {

	public T elem();

	public IListTerm<T> tail();

	public T get(int idx);

	public IListTerm<T> prefixAll(IListTerm<T> prefix);

	public IListTerm<T> prefix(T prefix);

	public T[] toArray();

	public IListTerm<T> reverse();

	public IListTerm<T> drop(int numElems);

	@Override
	public int size();

}
