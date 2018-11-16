package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class FalseLiteralTermBuild extends TermBuild {

	public FalseLiteralTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean executeFalse() {
		return false;
	}

}