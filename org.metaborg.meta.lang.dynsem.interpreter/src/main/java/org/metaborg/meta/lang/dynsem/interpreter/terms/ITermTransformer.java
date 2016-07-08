/**
 * 
 */
package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author vladvergu
 *
 */
public interface ITermTransformer {

	public IStrategoTerm transform(IStrategoTerm term);

	public final class IDENTITY implements ITermTransformer {

		@Override
		public IStrategoTerm transform(IStrategoTerm term) {
			return term;
		}
	}
}
