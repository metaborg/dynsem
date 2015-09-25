package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public class L_String extends AbstractNodeList<String> {

	public L_String(SourceSection src) {
		super(src);
	}

	public L_String(SourceSection source, String head,
			AbstractNodeList<String> tail) {
		super(source, head, tail);
	}

	@Override
	public L_String tail() {
		return (L_String) super.tail();
	}

	public static L_String fromStrategoTerm(IStrategoTerm alist) {
		SourceSection src = SourceSectionUtil.fromStrategoTerm(alist);
		L_String list = new L_String(src);
		for (int i = alist.getSubtermCount() - 1; i >= 0; i--) {
			String s = ((IStrategoString) alist.getSubterm(i)).stringValue();
			list = new L_String(src, s, list);
		}
		return list;
	}

}
