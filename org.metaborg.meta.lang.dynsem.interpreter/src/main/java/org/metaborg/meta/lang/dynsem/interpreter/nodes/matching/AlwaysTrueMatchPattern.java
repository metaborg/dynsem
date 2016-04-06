package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class AlwaysTrueMatchPattern extends MatchPattern {

	public AlwaysTrueMatchPattern(SourceSection source) {
		super(source);
	}

	@Override
	public boolean execute(Object term, VirtualFrame frame) {
		return true;
	}

	public static AlwaysTrueMatchPattern create(IStrategoAppl t) {
		CompilerAsserts.neverPartOfCompilation();
		return new AlwaysTrueMatchPattern(SourceSectionUtil.fromStrategoTerm(t));
	}

}
