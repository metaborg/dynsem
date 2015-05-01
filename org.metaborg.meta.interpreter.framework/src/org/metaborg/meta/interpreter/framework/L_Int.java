package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class L_Int extends AbstractPrimitiveList<Integer> {

	public L_Int(INodeSource source) {
		super(source);
	}

	public L_Int(INodeSource source, Integer head,
			AbstractPrimitiveList<Integer> tail) {
		super(source, head, tail);
	}

	@Override
	public L_Int tail() {
		return (L_Int) super.tail();
	}

	@Override
	public L_Int fromStrategoTerm(IStrategoTerm alist) {
		L_Int list = new L_Int(NodeSource.fromStrategoTerm(alist));
		for (int i = alist.getSubtermCount() - 1; i >= 0; i--) {
			int iv = ((IStrategoInt) alist.getSubterm(i)).intValue();
			list = new L_Int(NodeSource.fromStrategoTerm(alist), iv, list);
		}
		return list;
	}

}
