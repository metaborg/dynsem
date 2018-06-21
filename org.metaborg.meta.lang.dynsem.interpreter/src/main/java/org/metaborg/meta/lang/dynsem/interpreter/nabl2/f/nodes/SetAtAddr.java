package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.ArrayAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "addr", type = TermBuild.class), @NodeChild(value = "val", type = TermBuild.class) })
public abstract class SetAtAddr extends NativeOpBuild {

	public SetAtAddr(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "addr.key() == key_cached", "shape_cached.check(addr.frame())" })
	public Object doSetCached(FrameAddr addr, Object val, @Cached("addr.key()") Occurrence key_cached,
			@Cached("addr.frame().getShape()") Shape shape_cached,
			@Cached("shape_cached.getProperty(key_cached)") Property slot_property) {
		try {
			slot_property.set(addr.frame(), val, shape_cached);
		} catch (IncompatibleLocationException | FinalLocationException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	@Specialization(replaces = "doSetCached")
	public Object doSet(FrameAddr addr, Object val) {
		addr.frame().set(addr.key(), val);
		return val;
	}

	@Specialization
	public Object executeArraySet(ArrayAddr addr, Object val) {
		addr.arr().set(addr.idx(), val);
		return val;
	}

	public static SetAtAddr create(SourceSection source, TermBuild addr, TermBuild val) {
		return FrameNodeFactories.createSetAddr(source, addr, val);
	}

}
