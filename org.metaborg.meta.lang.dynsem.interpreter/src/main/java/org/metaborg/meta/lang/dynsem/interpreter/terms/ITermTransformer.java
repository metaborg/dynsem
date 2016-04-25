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

	public class IDENTITY implements ITermTransformer {

		public static final ITermTransformer INSTANCE = new IDENTITY();

		private IDENTITY() {

		}

		@Override
		public IStrategoTerm transform(IStrategoTerm term) {
			return term;
		}
	}
}
