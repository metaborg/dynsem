package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frame", type = TermBuild.class),
		@NodeChild(value = "dec", type = TermBuild.class) })
public abstract class GetFrameSlot extends NativeOpBuild {

	public GetFrameSlot(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "dec == dec_cached", "shape_cached.check(frame)" }, limit = "20")
	public Object doGetCached(DynamicObject frame, Occurrence dec, @Cached("dec") Occurrence dec_cached,
			@Cached("frame.getShape()") Shape shape_cached,
			@Cached("shape_cached.getProperty(dec_cached)") Property slot_property) {
		return slot_property.get(frame, shape_cached);
	}

	@Specialization(replaces = "doGetCached")
	public Object doGet(DynamicObject frame, Occurrence dec) {
		return frame.get(dec);
	}

	public static GetFrameSlot create(SourceSection source, TermBuild frame, TermBuild dec) {
		return GetFrameSlotNodeGen.create(source, frame, dec);
	}
}
