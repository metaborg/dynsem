package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NoOpPattern extends MatchPattern {

	public NoOpPattern(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean executeMatch() {
		return true;
	}

	public static NoOpPattern create(IStrategoAppl t) {
		CompilerAsserts.neverPartOfCompilation();
		return NoOpPatternNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

}
