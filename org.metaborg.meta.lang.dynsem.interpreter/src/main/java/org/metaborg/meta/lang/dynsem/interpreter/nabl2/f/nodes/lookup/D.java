package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
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

public abstract class D extends PathStep {

	protected final Occurrence dec;

	public D(ScopeIdentifier scopeIdent, Occurrence dec) {
		super(scopeIdent);
		this.dec = dec;
	}

	/*
	 * we cache the location. our BIG TIME assumption is that this instance of the node will only ever be called on
	 * frames of the same scopes!!!
	 */
	@Specialization(guards = { "shapeCheck(frm_shape, frm)" })
	public FrameAddr lookupCached(DynamicObject frm, @Cached("lookupShape(frm)") Shape frm_shape,
			@Cached("lookupLocation(frm_shape, dec)") Location loc) {
		assert FrameLayoutImpl.INSTANCE.getScope(frm).equals(scopeIdent);
		return new FrameAddr(frm, loc);
	}

	@Specialization(replaces = "lookupCached")
	public FrameAddr fallback(DynamicObject frm) {
		throw new IllegalStateException("Path<->scope instability");
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "D(" + scopeIdent + ", " + dec + ")";
	}

	public static D create(IStrategoAppl dTerm) {
		assert Tools.hasConstructor(dTerm, "D", 2);
		return DNodeGen.create(ScopeIdentifier.create(Tools.applAt(dTerm, 0)),
				Occurrence.create(Tools.applAt(dTerm, 1)));
	}

}
