package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "frm", type = TermBuild.class)
public abstract class CloneFrame extends NativeOpBuild {

	public CloneFrame(SourceSection source) {
		super(source);
	}

	public abstract DynamicObject executeWithEvaluatedFrame(DynamicObject frm);

	@Specialization(limit = "10", guards = { "frm.getShape() == cached_shape" })
	@ExplodeLoop
	public DynamicObject executeCachedShape(DynamicObject frm, @Cached("frm.getShape()") Shape cached_shape,
			@Cached(value = "getLocations(cached_shape)", dimensions = 1) Location[] locations) {
		DynamicObject clone = cached_shape.newInstance();
		try {
			for (int i = 0; i < locations.length; i++) {
				final Location loc = locations[i];
				loc.set(clone, loc.get(frm));
			}
		} catch (IncompatibleLocationException | FinalLocationException e) {
			throw new IllegalStateException(e);
		}
		return clone;
	}

	@Specialization(replaces = "executeCachedShape")
	public DynamicObject executeClone(DynamicObject frm) {
		return frm.copy(frm.getShape());
	}

	protected Location[] getLocations(Shape cached_shape) {
		CompilerAsserts.neverPartOfCompilation();
		List<Property> props = cached_shape.getPropertyListInternal(true);
		Location[] locations = new Location[props.size()];

		for (int i = 0; i < locations.length; i++) {
			locations[i] = props.get(i).getLocation();
		}
		return locations;
	}


	public static CloneFrame create(SourceSection source, TermBuild frm) {
		return CloneFrameNodeGen.create(source, frm);
	}

}
