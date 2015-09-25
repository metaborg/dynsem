package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public class L_Int extends AbstractNodeList<Integer> {

	public L_Int(SourceSection src) {
		super(src);
	}

	public L_Int(SourceSection source, Integer head,
			AbstractNodeList<Integer> tail) {
		super(source, head, tail);
	}

	@Override
	public L_Int tail() {
		return (L_Int) super.tail();
	}

	public static L_Int fromStrategoTerm(IStrategoTerm alist) {
		SourceSection src = SourceSectionUtil.fromStrategoTerm(alist);
		L_Int list = new L_Int(src);
		for (int i = alist.getSubtermCount() - 1; i >= 0; i--) {
			int iv = ((IStrategoInt) alist.getSubterm(i)).intValue();
			list = new L_Int(src, iv, list);
		}
		return list;
	}

}
