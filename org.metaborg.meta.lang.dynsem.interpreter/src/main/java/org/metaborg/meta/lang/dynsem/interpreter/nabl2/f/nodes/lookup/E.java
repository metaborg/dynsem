package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Shape;

public abstract class E extends PathStep {

	private final Label edgeLabel;
	protected final FrameLinkIdentifier linkIdent;

	@Child private PathStep next;

	public E(ScopeIdentifier scopeIdent, Label edgeLabel, PathStep next) {
		super(scopeIdent);
		this.edgeLabel = edgeLabel;
		this.next = next;
		this.linkIdent = new FrameLinkIdentifier(edgeLabel, next.scopeIdent);
	}

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
		return "E(" + edgeLabel + ", " + scopeIdent + ")";
	}

	public static E create(IStrategoAppl eTerm, PathStep next) {
		assert Tools.hasConstructor(eTerm, "E", 2);
		return ENodeGen.create(ScopeIdentifier.create(Tools.applAt(eTerm, 0)), Label.create(Tools.applAt(eTerm, 1)),
				next);
	}

}
