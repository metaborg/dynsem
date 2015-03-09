package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface INodeSource {

	public void apply(IStrategoTerm term);
}
