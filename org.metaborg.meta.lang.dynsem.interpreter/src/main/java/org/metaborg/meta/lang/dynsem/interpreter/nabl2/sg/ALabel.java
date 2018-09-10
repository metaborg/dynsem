package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public abstract class ALabel implements IApplTerm {
	@Override
	public Class<? extends IApplTerm> getSortClass() {
		return ALabel.class;
	}

	public static ALabel create(IStrategoTerm t) {
		assert Tools.isTermAppl(t);
		IStrategoAppl term = (IStrategoAppl) t;
		if (Tools.hasConstructor(term, "P", 0)) {
			return P.SINGLETON;
		}
		if (Tools.hasConstructor(term, "I", 0)) {
			return I.SINGLETON;
		}
		if (Tools.hasConstructor(term, "D", 0)) {
			return D.SINGLETON;
		}
		if (Tools.hasConstructor(term, "Label", 1)) {
			return new Label(Tools.javaStringAt(term, 0));
		}
		throw new IllegalStateException("Unsupported label term: " + term);
	}

}
