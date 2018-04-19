package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;

import com.oracle.truffle.api.object.DynamicObject;

public final class FrameLink {

	private final Label label;
	private final DynamicObject frame;
	private final FrameLinkIdentifier linkIdent;

	public FrameLink(Label label, DynamicObject frame) {
		this.label = label;
		this.frame = frame;
		this.linkIdent = new FrameLinkIdentifier(label, FrameLayoutImpl.INSTANCE.getScope(frame));
	}

	public Label label() {
		return label;
	}

	public DynamicObject frame() {
		return frame;
	}

	public FrameLinkIdentifier link() {
		return linkIdent;
	}
}
