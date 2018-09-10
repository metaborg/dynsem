package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;

public abstract class E extends PathStep {

	private final ALabel edgeLabel;
	protected final FrameEdgeIdentifier linkIdent;

	@Child private PathStep next;


	public E(ScopeIdentifier scopeIdent, ALabel edgeLabel, PathStep next) {
		super(scopeIdent);
		this.edgeLabel = edgeLabel;
		this.next = next;
		this.linkIdent = new FrameEdgeIdentifier(edgeLabel, next.scopeIdent);
	}

	@Override
	public Occurrence getTargetDec() {
		return next.getTargetDec();
	}

	@Specialization(guards = { "shape_cached.check(frm)" })
	public FrameAddr lookupCached(DynamicObject frm,
			@Cached("frm.getShape()") Shape shape_cached,
			@Cached("frm.getShape().getProperty(linkIdent)") Property linkProp) {
		DynamicObject nextFrame = FrameUtils.layout().getType().cast(linkProp.get(frm, shape_cached));
		return next.executeLookup(nextFrame);
	}

	@Specialization // (replaces = "lookupCached")
	public FrameAddr lookup(DynamicObject frm) {
		DynamicObject nextFrame = FrameUtils.layout().getType().cast(frm.get(linkIdent));
		assert FrameLayoutImpl.INSTANCE.getScope(nextFrame).equals(next.scopeIdent);
		return next.executeLookup(nextFrame);
	}

	@Override
	public void setNext(PathStep next) {
		assert next != null;
		CompilerDirectives.transferToInterpreterAndInvalidate();
		this.next = insert(next);
	}

	@TruffleBoundary
	@Override
	public String toString() {
		return "E(" + edgeLabel + ", " + scopeIdent + ") | " + next.toString();
	}

	public static E create(IStrategoAppl eTerm, PathStep next) {
		assert Tools.hasConstructor(eTerm, "E", 2);
		return ENodeGen.create(ScopeIdentifier.create(Tools.applAt(eTerm, 0)), ALabel.create(Tools.applAt(eTerm, 1)),
				next);
	}

}
