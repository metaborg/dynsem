package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class L_Int extends AbstractPrimitiveList<Integer> {

	public L_Int() {
		super();
	}

	public L_Int(Integer head, AbstractPrimitiveList<Integer> tail) {
		super(head, tail);
	}

	@Override
	public L_Int tail() {
		return (L_Int) super.tail();
	}

	@Override
	public L_Int fromStrategoTerm(IStrategoTerm alist) {
		L_Int list = new L_Int();
		for (int i = alist.getSubtermCount() - 1; i >= 0; i--) {
			int iv = ((IStrategoInt) alist.getSubterm(i)).intValue();
			list = new L_Int(iv, list);
		}
		return list;
	}

}
