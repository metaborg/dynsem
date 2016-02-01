package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class VarBind extends MatchPattern {

	private final String name;

	public VarBind(String name, SourceSection source) {
		super(source);
		this.name = name;
	}

	@Override
	public boolean execute(Object term, VirtualFrame frame) {
		FrameSlot slot = frame.getFrameDescriptor().findFrameSlot(name);
		if (frame.getValue(slot) == null) {
			frame.setObject(slot, term);
			return true;
		}
		throw new RuntimeException("Variable already bound");
	}

}
