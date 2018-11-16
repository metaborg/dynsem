package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TrueLiteralTermBuild extends TermBuild {

	public TrueLiteralTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean executeTrue() {
		return true;
	}

}