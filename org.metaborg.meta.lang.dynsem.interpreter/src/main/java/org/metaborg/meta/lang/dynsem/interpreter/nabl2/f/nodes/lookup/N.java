package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
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

public abstract class N extends PathStep {

	private final ALabel importLabel;
	private final Occurrence importRef;
	protected final FrameImportIdentifier linkIdent;

	@Child private PathStep next;

	public N(ScopeIdentifier scopeIdent, ALabel importLabel, Occurrence importRef, PathStep next) {
		super(scopeIdent);
		this.importLabel = importLabel;
		this.importRef = importRef;
		this.next = next;
		this.linkIdent = new FrameImportIdentifier(importLabel, importRef);
	}

	@Override
	public Occurrence getTargetDec() {
		return next.getTargetDec();
	}

	@Specialization(guards = { "shape_cached.check(frm)" })
	public FrameAddr lookupCached(DynamicObject frm, @Cached("frm.getShape()") Shape shape_cached,
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
		return "N(" + scopeIdent + ", " + importLabel + ", " + importRef + ") | " + next.toString();
	}

	public static N create(IStrategoAppl nTerm, PathStep next) {
		assert Tools.hasConstructor(nTerm, "N", 4);
		return NNodeGen.create(ScopeIdentifier.create(Tools.applAt(nTerm, 0)), ALabel.create(Tools.applAt(nTerm, 1)),
				Occurrence.create(Tools.applAt(nTerm, 2)), next);
	}

}
