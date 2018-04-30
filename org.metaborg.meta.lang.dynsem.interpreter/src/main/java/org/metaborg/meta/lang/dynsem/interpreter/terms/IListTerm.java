package org.metaborg.meta.lang.dynsem.interpreter.terms;

/**
 * Interface for all list terms.
 * 
 * @author vladvergu
 *
 * @param <T>
 *            type of the elements in this list
 */
public interface IListTerm extends ITerm {

	public Object head();

	public IListTerm tail();

	public Object[] take(int n);

	public IListTerm drop(int n);

	public IListTerm prefix(Object[] elems);


	// public Assumption getFixedLengthAssumption();
}
