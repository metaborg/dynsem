package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
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

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "link", type = TermBuild.class) })
public abstract class AddFrameLink extends NativeOpBuild {

	public AddFrameLink(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "shape_cached.check(frm)", "linkId_cached.equals(link.link())" })
	public DynamicObject doLinkCached(DynamicObject frm, FLink link, @Cached("frm.getShape()") Shape shape_cached,
			@Cached("link.link()") FrameLinkIdentifier linkId_cached,
			@Cached("shape_cached.getProperty(linkId_cached)") Property property_cached) {

		try {
			property_cached.set(frm, link.frame(), shape_cached);
		} catch (IncompatibleLocationException | FinalLocationException e) {
			throw new IllegalStateException(e);
		}

		return frm;
	}

	@Specialization
	public DynamicObject doLink(DynamicObject frm, FLink link) {
		frm.set(link.link(), link.frame());
		return frm;
	}

	public static AddFrameLink create(SourceSection source, TermBuild frm, TermBuild link) {
		return FrameNodeFactories.createAddFrameLink(source, frm, link);
	}

}
