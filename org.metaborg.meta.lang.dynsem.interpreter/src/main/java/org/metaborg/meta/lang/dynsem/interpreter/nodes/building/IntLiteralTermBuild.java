package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IntLiteralTermBuild extends TermBuild {

	private final int val;

	public IntLiteralTermBuild(SourceSection source, int val) {
		super(source);
		this.val = val;
	}

	@Specialization
	public int executeInteger() {
		return val;
	}

}