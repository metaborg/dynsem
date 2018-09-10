package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

public abstract class D extends PathStep {

	protected final Occurrence dec;

	public D(ScopeIdentifier scopeIdent, Occurrence dec) {
		super(scopeIdent);
		this.dec = dec;
	}

	@Override
	public Occurrence getTargetDec() {
		return dec;
	}

	@Specialization
	public FrameAddr lookup(DynamicObject frm) {
		return new FrameAddr(frm, dec);
	}

	@Override
	public void setNext(PathStep next) {
		throw new IllegalStateException("Cannot add next path step after D step");
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
