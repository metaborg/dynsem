package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class NoOpPattern extends MatchPattern {

	public NoOpPattern(SourceSection source) {
		super(source);
	}

	@Override
	public void executeMatch(VirtualFrame frame, Object term) {
	}

	public static NoOpPattern create(IStrategoAppl t) {
		CompilerAsserts.neverPartOfCompilation();
		return new NoOpPattern(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

}
