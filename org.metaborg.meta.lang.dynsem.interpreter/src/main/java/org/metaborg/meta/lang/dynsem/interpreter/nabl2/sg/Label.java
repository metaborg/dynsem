package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

public enum Label {
	P, I;

	public static Label create(IStrategoAppl term) {
		if (Tools.hasConstructor(term, "P", 0)) {
			return P;
		}
		if (Tools.hasConstructor(term, "I", 0)) {
			return I;
		}
		throw new IllegalStateException("Unsupported label term: " + term);
	}

}
