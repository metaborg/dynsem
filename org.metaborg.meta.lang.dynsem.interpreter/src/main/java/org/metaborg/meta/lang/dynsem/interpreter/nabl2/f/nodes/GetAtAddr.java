package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.ArrayAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "addr", type = TermBuild.class)
public abstract class GetAtAddr extends NativeOpBuild {

	public GetAtAddr(SourceSection source) {
		super(source);
	}

	@Specialization
	public Object executeGet(FrameAddr addr) {
		return addr.location().get(addr.frame());
	}

	@Specialization
	public Object executeArrayGet(ArrayAddr addr) {
		return addr.arr().get(addr.idx());
	}

	public static GetAtAddr create(SourceSection source, TermBuild addr) {
		return GetAtAddrNodeGen.create(source, addr);
	}
}
