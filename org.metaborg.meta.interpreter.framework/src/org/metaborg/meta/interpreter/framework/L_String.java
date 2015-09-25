package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.core.Tools;
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
		L_String list = new L_String(SourceSectionUtil.fromStrategoTerm(alist));
		for (IStrategoTerm elem : alist) {
			final SourceSection src = SourceSectionUtil.fromStrategoTerm(elem);
			list = new L_String(src, Tools.asJavaString(elem), list);
		}
		return list;
	}

}
