package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ArgRead extends TermBuild {

	private final int index;

	public ArgRead(int index, SourceSection source) {
		super(source);
		this.index = index;
	}

	@Override
	public ITerm execute(VirtualFrame frame) {
		return (ITerm) frame.getArguments()[index];
	}

}
