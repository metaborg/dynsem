package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.ArrayAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "addr", type = TermBuild.class)
public abstract class GetAtAddr extends NativeOpBuild {

	public GetAtAddr(SourceSection source) {
		super(source);
	}

	@Specialization(limit = "10", guards = { "addr.location() == cached_location" })
	public Object executeFrameGetCached(FrameAddr addr, @Cached("addr.location()") Location cached_location) {
		return cached_location.get(addr.frame());
	}

	@Specialization(replaces = "executeFrameGetCached")
	public Object executeFrameGet(FrameAddr addr) {
		return addr.frame().get(addr.key());
		// return addr.location().get(addr.frame());
	}

	@Specialization
	public Object executeArrayGet(ArrayAddr addr) {
		return addr.arr().get(addr.idx());
	}
}
