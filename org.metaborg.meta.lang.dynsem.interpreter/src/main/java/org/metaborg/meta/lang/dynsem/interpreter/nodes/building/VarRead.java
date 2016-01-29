package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class VarRead extends TermBuild {

	private final String name;

	public VarRead(String name, SourceSection source) {
		super(source);
		this.name = name;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		FrameSlot slot = frame.getFrameDescriptor().findFrameSlot(name);
		return frame.getValue(slot);
	}

}
