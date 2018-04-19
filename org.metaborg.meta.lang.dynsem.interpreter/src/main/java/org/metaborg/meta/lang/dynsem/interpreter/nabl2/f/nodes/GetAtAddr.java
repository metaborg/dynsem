package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.shared.ValSort;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "addr", type = TermBuild.class)
public abstract class GetAtAddr extends NativeOpBuild {

	public GetAtAddr(SourceSection source) {
		super(source);
	}

	// TODO: 2 cases: arrayaddr and frameaddr

	@Specialization
	public ValSort executeGet(Addr addr) {
		throw new IllegalStateException("Get not implemented");
	}

	public static GetAtAddr create(SourceSection source, TermBuild addr) {
		return GetAtAddrNodeGen.create(source, addr);
	}
}
