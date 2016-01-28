package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Premise extends Node {

	public Premise(SourceSection source) {
		super(source);
	}

	public abstract void execute(VirtualFrame frame);
}
