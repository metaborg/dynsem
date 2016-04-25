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
}
