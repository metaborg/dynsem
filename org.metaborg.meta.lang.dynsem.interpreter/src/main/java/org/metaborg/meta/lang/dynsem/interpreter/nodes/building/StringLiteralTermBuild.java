package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class StringLiteralTermBuild extends TermBuild {

	private final String val;

	public StringLiteralTermBuild(String val, SourceSection source) {
		super(source);
		this.val = val;
	}

	@Specialization
	public String executeCreate() {
		return val;
	}
}