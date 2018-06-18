package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public final class Label {

	public final static Label P = new Label("P");
	public final static Label I = new Label("I");

	public final String l;

	public Label(String l) {
		this.l = l;
	}

	public static Label create(IStrategoTerm t) {
		assert Tools.isTermAppl(t);
		IStrategoAppl term = (IStrategoAppl) t;
		if (Tools.hasConstructor(term, "P", 0)) {
			return P;
		}
		if (Tools.hasConstructor(term, "I", 0)) {
			return I;
		}
		if (Tools.hasConstructor(term, "Label", 1)) {
			return new Label(Tools.javaStringAt(term, 0));
		}
		throw new IllegalStateException("Unsupported label term: " + term);
	}

	@Override
	public String toString() {
		return l;
	}

	@Override
	public int hashCode() {
		return 37 * l.hashCode() + 9534;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof Label && ((Label) obj).l.equals(this.l));
	}
}
