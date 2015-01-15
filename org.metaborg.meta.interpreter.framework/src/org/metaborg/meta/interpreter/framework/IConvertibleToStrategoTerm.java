package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public interface IConvertibleToStrategoTerm {

	public IStrategoTerm toStrategoTerm(ITermFactory factory);
}
