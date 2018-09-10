package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.ArrayAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "addr", type = TermBuild.class)
public abstract class GetAtAddr extends NativeOpBuild {

	public GetAtAddr(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "addr.key() == key_cached", "shape_cached.check(addr.frame())" }, limit = "20")
	public Object doGetCached(FrameAddr addr, @Cached("addr.key()") Occurrence key_cached,
			@Cached("addr.frame().getShape()") Shape shape_cached,
			@Cached("shape_cached.getProperty(key_cached)") Property slot_property) {
		return slot_property.get(addr.frame(), shape_cached);
	}

	@Specialization // (replaces = "doGetCached")
	public Object doGet(FrameAddr addr) {
		return addr.frame().get(addr.key());
	}

	@Specialization
	public Object executeArrayGet(ArrayAddr addr) {
		return addr.arr().get(addr.idx());
	}

	public static GetAtAddr create(SourceSection source, TermBuild addr) {
		return FrameNodeFactories.createGetAtAddr(source, addr);
	}
}
