package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NilMatch extends ListMatchPattern {

	public NilMatch(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean doMatch(Nil cons) {
		return true;
	}

	@Specialization
	public boolean doMismatch(Cons cons) {
		return false;
	}

}
