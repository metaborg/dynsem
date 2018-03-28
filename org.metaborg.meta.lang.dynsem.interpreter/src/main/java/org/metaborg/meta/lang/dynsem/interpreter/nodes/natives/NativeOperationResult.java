package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import com.oracle.truffle.api.frame.Frame;

public class NativeOperationResult {

	private final Object term;
	private final Frame componentFrame;

	public NativeOperationResult(Object term, Frame components) {
		this.term = term;
		this.componentFrame = components;
	}

	public Object getTerm() {
		return term;
	}

	public Frame getComponentFrame() {
		return componentFrame;
	}
}
