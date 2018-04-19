package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "label", type = TermBuild.class),
		@NodeChild(value = "frm", type = TermBuild.class) })
@ImportStatic(FrameLayoutImpl.class)
public abstract class NewFrameLink extends NativeOpBuild {

	public NewFrameLink(SourceSection source) {
		super(source);
	}

	/*
	 * we want to cache the FrameLinkIdentifier if the label and the linked scope is constant the linked scope is
	 * constant if the shape of the frame is constant (because if two frames have the same shape then they have the same
	 * scope)
	 */
	@Specialization(guards = { "label == label_cached", "shapeCheck(frm_shape, frm)" })
	public FrameLink createCached(Label label, DynamicObject frm, @Cached("label") Label label_cached,
			@Cached("lookupShape(frm)") Shape frm_shape,
			@Cached("createLinkIdentifier(label, INSTANCE.getScope(frm))") FrameLinkIdentifier linkIdent) {
		return new FrameLink(label, frm, linkIdent);
	}

	@Specialization(replaces = "createCached")
	public FrameLink createUncached(Label label, DynamicObject frm) {
		return new FrameLink(label, frm);
	}

	protected boolean shapeCheck(Shape shape, DynamicObject frm) {
		return shape != null && shape.check(frm);
	}

	protected FrameLinkIdentifier createLinkIdentifier(Label label, ScopeIdentifier scopeIdent) {
		CompilerAsserts.neverPartOfCompilation();
		return new FrameLinkIdentifier(label, scopeIdent);
	}

	protected Shape lookupShape(DynamicObject frm) {
		CompilerAsserts.neverPartOfCompilation();
		assert FrameLayoutImpl.INSTANCE.isFrame(frm);
		return frm.getShape();
	}

	public static NewFrameLink create(SourceSection source, TermBuild label, TermBuild frm) {
		return NewFrameLinkNodeGen.create(source, label, frm);
	}

}
