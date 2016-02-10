/**
 * 
 */
package org.metaborg.meta.lang.dynsem.interpreter;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author vladvergu
 *
 */
public interface ITermTransformer {

	public IStrategoTerm transform(IStrategoTerm term);

	public static final class IdentityTermTransformer implements
			ITermTransformer {

		@Override
		public IStrategoTerm transform(IStrategoTerm term) {
			return term;
		}

	}
}
