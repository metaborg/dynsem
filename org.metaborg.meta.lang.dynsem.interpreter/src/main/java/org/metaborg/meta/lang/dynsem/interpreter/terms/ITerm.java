package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.spoofax.interpreter.terms.IStrategoTerm;

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

	/**
	 * Check whether this {@link ITerm} has an associated {@link IStrategoTerm}.
	 * 
	 * @return <code>true</code> if this {@link ITerm} has an associated {@link IStrategoTerm}, <code>false</code>
	 *         otherwise.
	 */
	public boolean hasStrategoTerm();

	/**
	 * Retrieve the {@link IStrategoTerm} that this instance of {@link ITerm} has been created from, if that exists.
	 * 
	 * @return the {@link IStrategoTerm} which this {@link ITerm} was created from, or <code>null</code> if this
	 *         {@link ITerm} was not created from a {@link IStrategoTerm} term (i.e. it was created directly).
	 */
	public IStrategoTerm getStrategoTerm();
}
