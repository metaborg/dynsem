package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class TermUtils {

	public static boolean boolFromTerm(IStrategoTerm term) {
		if (Tools.isTermAppl(term)) {
			IStrategoAppl tAppl = (IStrategoAppl) term;
			if (Tools.hasConstructor(tAppl, "$__DS_False__$", 0)) {
				return false;
			} else if (Tools.hasConstructor(tAppl, "$__DS_True__$", 0)) {
				return true;
			}
		}
		throw new RuntimeException("Malformed boolean: " + term);
	}

	public static IStrategoTerm termFromBool(boolean bV, ITermFactory factory) {
		return factory.makeAppl(factory.makeConstructor(bV ? "$__DS_True__$"
				: "$__DS_False__$", 0));
	}
}
