package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class VarRead extends TermBuild {

	private final FrameSlot slot;

	public VarRead(FrameSlot slot, SourceSection source) {
		super(source);
		this.slot = slot;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return frame.getValue(slot);
	}

}
