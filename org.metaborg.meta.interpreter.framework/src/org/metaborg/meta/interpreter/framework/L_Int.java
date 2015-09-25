package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.core.Tools;
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
		L_Int list = new L_Int(SourceSectionUtil.fromStrategoTerm(alist));
		for (IStrategoTerm elem : alist) {
			final SourceSection src = SourceSectionUtil.fromStrategoTerm(elem);
			list = new L_Int(src, Tools.asJavaInt(elem), list);
		}
		return list;
	}

}
