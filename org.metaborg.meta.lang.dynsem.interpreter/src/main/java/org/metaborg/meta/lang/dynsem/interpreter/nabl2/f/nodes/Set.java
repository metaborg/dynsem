package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.shared.ValSort;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "addr", type = TermBuild.class), @NodeChild(value = "val", type = TermBuild.class) })
public abstract class Set extends NativeOpBuild {

	public Set(SourceSection source) {
		super(source);
	}

	@Specialization
	public ValSort executeGet(Addr addr, ValSort val) {
		throw new IllegalStateException("Set not implemented");
	}

	public static Set create(SourceSection source, TermBuild addr, TermBuild val) {
		return SetNodeGen.create(source, addr, val);
	}
}
