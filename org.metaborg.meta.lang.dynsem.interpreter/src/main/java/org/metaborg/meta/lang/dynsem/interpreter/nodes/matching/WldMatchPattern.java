package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class WldMatchPattern extends MatchPattern {

	public WldMatchPattern(SourceSection source) {
		super(source);
	}

	@Override
	public void executeMatch(VirtualFrame frame, Object term) {

	}

	public static WldMatchPattern create(IStrategoAppl t) {
		CompilerAsserts.neverPartOfCompilation();
		return new WldMatchPattern(SourceSectionUtil.fromStrategoTerm(t));
	}

}
