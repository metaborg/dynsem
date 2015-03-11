package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class L_Double extends AbstractPrimitiveList<Double> {

	public L_Double() {
		super();
	}

	public L_Double(Double head, AbstractPrimitiveList<Double> tail) {
		super(head, tail);
	}

	@Override
	public L_Double tail() {
		return (L_Double) super.tail();
	}

	@Override
	public L_Double fromStrategoTerm(IStrategoTerm alist) {
		L_Double list = new L_Double();
		for (int i = alist.getSubtermCount(); i >= 0; i--) {
			double dv = ((IStrategoReal) alist.getSubterm(i)).realValue();
			list = new L_Double(dv, list);
		}
		return list;
	}

}
