package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;

import com.oracle.truffle.api.object.DynamicObject;

public final class FrameLink {

	private final DynamicObject frame;
	private final FrameLinkIdentifier linkIdent;

	public FrameLink(DynamicObject frame, FrameLinkIdentifier linkIdent) {
		this.frame = frame;
		this.linkIdent = linkIdent;
	}

	public DynamicObject frame() {
		return frame;
	}

	public FrameLinkIdentifier link() {
		return linkIdent;
	}
}
