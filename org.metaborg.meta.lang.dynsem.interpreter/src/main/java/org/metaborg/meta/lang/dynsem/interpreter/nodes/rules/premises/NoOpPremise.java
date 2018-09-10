package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NoOpPremise extends Premise {

	public NoOpPremise(SourceSection source) {
		super(source);
	}

	@Specialization
	public void execute() {
	}

}
