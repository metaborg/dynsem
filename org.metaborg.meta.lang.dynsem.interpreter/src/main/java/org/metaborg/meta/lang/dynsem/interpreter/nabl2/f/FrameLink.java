package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;

import com.oracle.truffle.api.object.DynamicObject;

public final class FrameLink {

	private final ALabel label;
	private final DynamicObject frame;
	private final FrameLinkIdentifier linkIdent;

	public FrameLink(ALabel label, DynamicObject frame, FrameLinkIdentifier linkIdent) {
		this.label = label;
		this.frame = frame;
		this.linkIdent = linkIdent;
	}

	public ALabel label() {
		return label;
	}

	public DynamicObject frame() {
		return frame;
	}

	public FrameLinkIdentifier link() {
		return linkIdent;
	}
}
