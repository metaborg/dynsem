package org.metaborg.meta.lang.dynsem.interpreter.terms;

/**
 * Most generic interface of all terms used in DynSem interpreters.
 * 
 * @author vladvergu
 *
 */
public interface ITerm {

	/**
	 * Compute and return the size of this {@link ITerm}. The size of the {@link ITerm} is equal to the number of
	 * subterms.
	 * 
	 * @return the size of this {@link ITerm}
	 */
	public int size();

	public Object subterm(int idx);

	public Object[] subterms();

	public String sort();
	

}
