package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.source.SourceSection;

public final class ArrayNodeFactories {

	private ArrayNodeFactories() {
	}

	public static NewArray createNewArray(SourceSection source, TermBuild len, TermBuild val) {
		return NewArrayNodeGen.create(source, len, val);
	}


}
