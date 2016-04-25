package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.spoofax.interpreter.terms.IStrategoTerm;

public final class IdentityTransformer implements ITermTransformer {

	@Override
	public IStrategoTerm transform(IStrategoTerm term) {
		return term;
	}

}