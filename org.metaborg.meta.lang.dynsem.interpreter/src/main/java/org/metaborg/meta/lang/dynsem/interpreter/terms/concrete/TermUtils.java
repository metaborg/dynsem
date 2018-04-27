package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public final class TermUtils {

	private TermUtils() {
	}

	public static Object termFromStratego(IStrategoTerm t) {
		if (Tools.isTermAppl(t)) {
			IStrategoAppl at = (IStrategoAppl) t;
			return new ApplTerm(null, at.getName(), termsFromStratego(at.getAllSubterms()), at);
		}

		if (Tools.isTermTuple(t)) {
			IStrategoTuple at = (IStrategoTuple) t;
			return new TupleTerm(null, termsFromStratego(at.getAllSubterms()), at);
		}

		if (Tools.isTermList(t)) {
			IStrategoList at = (IStrategoList) t;
			return new ListTerm(null, termsFromStratego(at.getAllSubterms()), at);
		}

		if (Tools.isTermString(t)) {
			return Tools.asJavaString(t);
		}

		if (Tools.isTermInt(t)) {
			return Tools.asJavaInt(t);
		}

		throw new IllegalStateException("unknown term: " + t);
	}

	public static Object[] termsFromStratego(IStrategoTerm[] sts) {
		Object[] ts = new Object[sts.length];
		for (int i = 0; i < ts.length; i++) {
			ts[i] = termFromStratego(sts[i]);
		}
		return ts;
	}


}
