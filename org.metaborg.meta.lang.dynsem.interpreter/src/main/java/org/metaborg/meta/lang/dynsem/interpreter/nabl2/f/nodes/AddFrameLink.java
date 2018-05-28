package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "link", type = TermBuild.class) })
public abstract class AddFrameLink extends NativeOpBuild {

	public AddFrameLink(SourceSection source) {
		super(source);
	}

	/*
	 * Cache the location in the frame to set
	 * 
	 * - this means that we cache the location of the property which has link.link() as its key.
	 * 
	 * - the cache is valid if 1) the shape of the frame is constant and if 2) the link.link() is identity constant
	 */
	@Specialization(guards = { "link.link() == linkIdent", "shapeCheck(frm_shape, frm)" })
	public DynamicObject linkCached(DynamicObject frm, FrameLink link, @Cached("lookupShape(frm)") Shape frm_shape,
			@Cached("link.link()") FrameLinkIdentifier linkIdent,
			@Cached("lookupLocation(frm_shape, linkIdent)") Location location) {
		try {
			location.set(frm, link.frame(), frm_shape);
		} catch (IncompatibleLocationException | FinalLocationException e) {
			throw new IllegalStateException(e);
		}

		return frm;
	}

	@Specialization(replaces = "linkCached")
	public DynamicObject linkUncached(DynamicObject frm, FrameLink link) {
		frm.set(link.link(), link.frame());
		return frm;
	}

	protected Shape lookupShape(DynamicObject frm) {
		CompilerAsserts.neverPartOfCompilation();
		assert FrameLayoutImpl.INSTANCE.isFrame(frm);
		return frm.getShape();
	}

	protected boolean shapeCheck(Shape shape, DynamicObject frm) {
		return shape != null && shape.check(frm);
	}

	protected Location lookupLocation(Shape shape, FrameLinkIdentifier linkIdent) {
		CompilerAsserts.neverPartOfCompilation();
		Property property = shape.getProperty(linkIdent);
		if (property == null) {
			throw new ReductionFailure(
					"Slot for link " + linkIdent + " does not exist for frame of scope "
							+ FrameLayoutImpl.INSTANCE.getScope(shape.createFactory()),
					InterpreterUtils.createStacktrace(), this);
		}
		return property.getLocation();
	}

}
