package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Premise extends DynSemNode {

	public Premise(SourceSection source) {
		super(source);
	}

	public abstract void execute(VirtualFrame frame);

}
