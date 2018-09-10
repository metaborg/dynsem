package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.Array;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "len", type = TermBuild.class), @NodeChild(value = "val", type = TermBuild.class) })
public abstract class NewArray extends NativeOpBuild {

	public NewArray(SourceSection source) {
		super(source);
	}

	@Specialization
	public Array executeNewArray(int len, Object val) {
		return new Array(len, val);
	}

	public static NewArray create(SourceSection source, TermBuild len, TermBuild val) {
		return ArrayNodeFactories.createNewArray(source, len, val);
	}
}
