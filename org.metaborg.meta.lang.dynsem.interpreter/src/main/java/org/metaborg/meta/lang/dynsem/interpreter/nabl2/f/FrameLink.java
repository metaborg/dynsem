package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;

import com.oracle.truffle.api.object.DynamicObject;

public final class FrameLink {

	private final Label label;
	private final DynamicObject frame;

	public FrameLink(Label label, DynamicObject frame) {
		this.label = label;
		this.frame = frame;
	}

}
