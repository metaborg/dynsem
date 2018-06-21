package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "label", type = TermBuild.class), @NodeChild(value = "occ", type = TermBuild.class),
		@NodeChild(value = "frm", type = TermBuild.class) })
// @ImportStatic(FrameLayoutImpl.class)
public abstract class NewFrameImportLink extends NativeOpBuild {

	public NewFrameImportLink(SourceSection source) {
		super(source);
	}

	/*
	 * we want to cache the FrameLinkIdentifier if the label and the linked scope is constant the linked scope is
	 * constant if the shape of the frame is constant (because if two frames have the same shape then they have the same
	 * scope)
	 */
	@Specialization(guards = { "label == label_cached", "occ == occ_cached", "shapeCheck(frm_shape, frm)" })
	public FrameLink createCached(ALabel label, Occurrence occ, DynamicObject frm, @Cached("label") ALabel label_cached,
			@Cached("occ") Occurrence occ_cached, @Cached("lookupShape(frm)") Shape frm_shape,
			@Cached("createLinkIdentifier(label, occ)") FrameImportIdentifier linkIdent) {
		return new FrameLink(frm, linkIdent);
	}

	@Specialization(replaces = "createCached")
	public FrameLink createUncached(ALabel label, Occurrence occ, DynamicObject frm) {
		return new FrameLink(frm, createLinkIdentifier(label, occ));
	}

	protected boolean shapeCheck(Shape shape, DynamicObject frm) {
		return shape != null && shape.check(frm);
	}

	protected FrameImportIdentifier createLinkIdentifier(ALabel label, Occurrence occ) {
		CompilerAsserts.neverPartOfCompilation();
		return new FrameImportIdentifier(label, occ);
	}

	protected Shape lookupShape(DynamicObject frm) {
		CompilerAsserts.neverPartOfCompilation();
		assert FrameLayoutImpl.INSTANCE.isFrame(frm);
		return frm.getShape();
	}

	public static NewFrameImportLink create(SourceSection source, TermBuild label, TermBuild occ, TermBuild frm) {
		return FrameNodeFactories.createNewFrameImportLink(source, label, occ, frm);
	}

}
