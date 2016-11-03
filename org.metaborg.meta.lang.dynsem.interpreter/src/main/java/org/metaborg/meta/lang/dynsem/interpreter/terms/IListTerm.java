package org.metaborg.meta.lang.dynsem.interpreter.terms;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Interface for all list terms.
 * 
 * @author vladvergu
 *
 * @param <T>
 *            type of the elements in this list
 */
public interface IListTerm<T> extends ITerm {

	/**
	 * @return the n-th element of this list, or <code>null</code> if the list is too short.
	 */
	public T get(int n);

	/**
	 * 
	 * @return the first element of this list, or <code>null</code> if the list is empty.
	 */
	public T head();

	/**
	 * 
	 * @param numElems
	 *            the number of elements to select from the array
	 * @return an array containing the first <code>numElems</code> elements of this list
	 * @throws NoSuchElementException
	 *             if the {@link #size()} of this list is less than <code>numElems</code>
	 */
	public T[] take(int numElems);

	/**
	 * @return an instance of {@link IListTerm<T>} containing all but the first element of this list.
	 * @throws NoSuchElementException
	 *             if this {@link IListTerm} is empty
	 */
	public IListTerm<T> tail();

	/**
	 * 
	 * Drop a number of elements from the head of this list, returning an {@link IListTerm} for the remaining elements.
	 * 
	 * @param numElems
	 *            the number of elements to remove from the head
	 * @return a {@link IListTerm} for the remainder of this list
	 * @throws NoSuchElementException
	 *             if the {@link #size()} of this {@link IListTerm} is less than <code>numElems</<code>
	 */
	public IListTerm<T> drop(int numElems);

	/**
	 * Append an element to this list
	 * 
	 * @param elem
	 *            the element to be appended
	 * @return a new {@link IListTerm} containing all elements of this {@link IListTerm} and the given <code>elem</code>
	 *         at the last position.
	 */
	public IListTerm<T> add(T elem);

	/**
	 * Batch append multiple elements to this {@link IListTerm} to obtain a new list
	 * 
	 * @param elems
	 *            an array of elements to be appended
	 * @return a new {@link IListTerm} containing the elements of this {@link IListTerm} followed by the elements in the
	 *         <code>elems</code> array.
	 */
	public IListTerm<T> addAll(T[] elems);

	/**
	 * 
	 * @return an iterator for this {@link IListTerm}
	 */
	public Iterator<T> iterator();

}
