package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class VarBind extends MatchPattern {

	private final FrameSlot slot;

	public VarBind(FrameSlot slot, SourceSection source) {
		super(source);
		this.slot = slot;
	}

	@Override
	public boolean execute(Object term, VirtualFrame frame) {
		frame.setObject(slot, term);
		return true;
	}

}
