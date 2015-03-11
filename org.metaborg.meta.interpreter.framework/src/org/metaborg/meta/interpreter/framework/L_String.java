package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class L_String extends AbstractPrimitiveList<String> {

	public L_String() {
		super();
	}

	public L_String(String head, AbstractPrimitiveList<String> tail) {
		super(head, tail);
	}

	@Override
	public L_String tail() {
		return (L_String) super.tail();
	}
	
	@Override
	public L_String fromStrategoTerm(IStrategoTerm alist) {
		L_String list = new L_String();
		for (int i = alist.getSubtermCount(); i >= 0; i--) {
			String s = ((IStrategoString) alist.getSubterm(i)).stringValue();
			list = new L_String(s, list);
		}
		return list;
	}

}
