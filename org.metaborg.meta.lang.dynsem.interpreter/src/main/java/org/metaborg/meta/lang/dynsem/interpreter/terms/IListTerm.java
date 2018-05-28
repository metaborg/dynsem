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

	// /**
	// * @return the n-th element of this list, or <code>null</code> if the list is too short.
	// */
	// public T get(int n);
	//
	// /**
	// *
	// * @return the first element of this list, or <code>null</code> if the list is empty.
	// */
	// public T head();
	//
	// /**
	// *
	// * @param numElems
	// * the number of elements to select from the array
	// * @return an array containing the first <code>numElems</code> elements of this list
	// * @throws NoSuchElementException
	// * if the {@link #size()} of this list is less than <code>numElems</code>
	// */
	// public T[] take(int numElems);
	//
	// /**
	// * @return an instance of {@link IListTerm<T>} containing all but the first element of this list.
	// * @throws NoSuchElementException
	// * if this {@link IListTerm} is empty
	// */
	// public IListTerm<T> tail();
	//
	// /**
	// *
	// * Drop a number of elements from the head of this list, returning an {@link IListTerm} for the remaining
	// elements.
	// *
	// * @param numElems
	// * the number of elements to remove from the head
	// * @return a {@link IListTerm} for the remainder of this list
	// * @throws NoSuchElementException
	// * if the {@link #size()} of this {@link IListTerm} is less than <code>numElems</<code>
	// */
	// public IListTerm<T> drop(int numElems);
	//
	// /**
	// * Prepend an element to this list
	// *
	// * @param elem
	// * the element to be appended
	// * @return a new {@link IListTerm} containing the new element followed by the elements in of this {@link
	// IListTerm}
	// */
	// public IListTerm<T> add(T elem);
	//
	// /**
	// * Batch prepend multiple elements to this {@link IListTerm} to obtain a new list
	// *
	// * @param elems
	// * an array of elements to be appended
	// * @return a new {@link IListTerm} containing the prepended elements from <code>elems</code> followed by the
	// * elements of this {@link IListTerm}
	// */
	// public IListTerm<T> addAll(T[] elems);
	//
	// /**
	// * Reverse this list. The new list will contain the same elements but in reverse order
	// *
	// * @return a new {@link IListTerm} containing the same items but in reverse order
	// */
	// public IListTerm<T> reverse();
	//
	// /**
	// *
	// * @return an iterator for this {@link IListTerm}
	// */
	// @Override
	// public Iterator<T> iterator();
	//
	// /**
	// * @return an array containing all of the elements in this list
	// */
	// public T[] toArray();

}
