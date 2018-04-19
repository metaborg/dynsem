package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Shape;

public abstract class N extends PathStep {

	private final Label importLabel;
	private final Occurrence importRef;
	protected final FrameLinkIdentifier linkIdent;

	@Child private PathStep next;

	public N(ScopeIdentifier scopeIdent, Label importLabel, Occurrence importRef, PathStep next) {
		super(scopeIdent);
		this.importLabel = importLabel;
		this.importRef = importRef;
		this.next = next;
		this.linkIdent = new FrameLinkIdentifier(importLabel, next.scopeIdent);
	}

	// FIXME: this feels like the wrong semantics
	@Specialization(guards = { "shapeCheck(frm_shape, frm)" })
	public FrameAddr lookupCached(DynamicObject frm, @Cached("lookupShape(frm)") Shape frm_shape,
			@Cached("lookupLocation(frm_shape, linkIdent)") Location loc) {
		DynamicObject nextFrame = FrameLayoutUtil.layout().getType().cast(loc.get(frm, frm_shape));
		assert FrameLayoutImpl.INSTANCE.getScope(nextFrame).equals(next.scopeIdent);
		return next.executeLookup(nextFrame);
	}

	@Specialization(replaces = "lookupCached")
	public FrameAddr fallback(DynamicObject frm) {
		throw new IllegalStateException("Path<->scope instability");
	}

	@TruffleBoundary
	@Override
	public String toString() {
		return "N(" + scopeIdent + ", " + importLabel + ", " + importRef + ", " + next + ")";
	}

	public static N create(IStrategoAppl nTerm) {
		assert Tools.hasConstructor(nTerm, "N", 4);
		return NNodeGen.create(ScopeIdentifier.create(Tools.applAt(nTerm, 0)), Label.create(Tools.applAt(nTerm, 1)),
				Occurrence.create(Tools.applAt(nTerm, 2)), Path.createSteps(Tools.listAt(nTerm, 3)));
	}
}
