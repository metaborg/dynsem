package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "dec", type = TermBuild.class),
		@NodeChild(value = "val", type = TermBuild.class) })
public abstract class SetFrameSlot extends NativeOpBuild {

	public SetFrameSlot(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "dec == dec_cached", "shape_cached.check(frm)" }, limit = "20")
	public Object doSetCached(DynamicObject frm, Occurrence dec, Object val, @Cached("dec") Occurrence dec_cached,
			@Cached("frm.getShape()") Shape shape_cached,
			@Cached("shape_cached.getProperty(dec_cached)") Property slot_property) {
		try {
			slot_property.set(frm, val, shape_cached);
		} catch (IncompatibleLocationException | FinalLocationException e) {
			throw new IllegalStateException(e);
		}
		return val;
	}

	@Specialization(replaces = "doSetCached")
	public Object doSet(DynamicObject frm, Occurrence dec, Object val) {
		frm.set(dec, val);
		return val;
	}

	public static SetFrameSlot create(SourceSection source, TermBuild frame, TermBuild dec, TermBuild val) {
		return SetFrameSlotNodeGen.create(source, frame, dec, val);
	}

}
