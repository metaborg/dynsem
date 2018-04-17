package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.shared.ValSort;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "addr", type = TermBuild.class)
public abstract class Get extends NativeOpBuild {

	public Get(SourceSection source) {
		super(source);
	}

	@Specialization
	public ValSort executeGet(Addr addr) {
		throw new RuntimeException("Get not implemented");
	}

	public static Get create(SourceSection source, TermBuild addr) {
		return GetNodeGen.create(source, addr);
	}
}
