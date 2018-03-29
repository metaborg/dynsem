package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class CaptureComponentNode extends DynSemNode {

	@Child private TermBuild tb;
	
	private final FrameSlot componentSlot;

	public CaptureComponentNode(SourceSection source, FrameSlot compSlot, TermBuild tb) {
		super(source);
		this.componentSlot = compSlot;
		this.tb = tb;
	}

	public void executeCapture(VirtualFrame frame, VirtualFrame components) {
		components.setObject(componentSlot, tb.executeGeneric(frame));
	}

}
