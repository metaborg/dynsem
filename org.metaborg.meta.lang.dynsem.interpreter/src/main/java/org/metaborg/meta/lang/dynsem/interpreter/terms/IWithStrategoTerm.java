package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IWithStrategoTerm {

	public boolean hasStrategoTerm();

	/**
	 * Retrieve the {@link IStrategoTerm} that this instance of {@link ITerm} has been created from, if that exists.
	 * 
	 * @return the {@link IStrategoTerm} which this {@link ITerm} was created from, or <code>null</code> if this
	 *         {@link ITerm} was not created from a {@link IStrategoTerm} term (i.e. it was created directly).
	 */
	public IStrategoTerm getStrategoTerm();
}
