package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Fresh extends NativeOpBuild {

	private static int next = 0;

	public Fresh(SourceSection source) {
		super(source);
	}

	@Specialization
	public int execIncrement() {
		return next++;
	}

}
