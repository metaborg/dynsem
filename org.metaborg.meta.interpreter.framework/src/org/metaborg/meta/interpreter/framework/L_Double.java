package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public class L_Double extends AbstractNodeList<Double> {

	public L_Double(SourceSection src) {
		super(src);
	}

	public L_Double(SourceSection src, Double head, L_Double tail) {
		super(src, head, tail);
	}

	public static L_Double fromStrategoTerm(IStrategoTerm alist) {
		L_Double list = new L_Double(SourceSectionUtil.fromStrategoTerm(alist));
		for (IStrategoTerm elem : alist) {
			final SourceSection src = SourceSectionUtil.fromStrategoTerm(elem);
			list = new L_Double(src, Tools.asJavaDouble(elem), list);
		}
		return list;
	}
}
