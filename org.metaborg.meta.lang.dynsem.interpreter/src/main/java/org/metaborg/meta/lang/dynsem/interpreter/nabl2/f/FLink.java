package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.object.DynamicObject;

public abstract class FLink implements IApplTerm {

	private final ALabel label;
	private final DynamicObject frm;
	private final FrameLinkIdentifier identifier;

	public FLink(ALabel label, DynamicObject frm, FrameLinkIdentifier identifier) {
		this.label = label;
		this.frm = frm;
		this.identifier = identifier;
	}

	@Override
	public final Class<?> getSortClass() {
		return FLink.class;
	}

	public final FrameLinkIdentifier link() {
		return this.identifier;
	}

	public final ALabel label() {
		return this.label;
	}

	public final DynamicObject frame() {
		return this.frm;
	}

}
