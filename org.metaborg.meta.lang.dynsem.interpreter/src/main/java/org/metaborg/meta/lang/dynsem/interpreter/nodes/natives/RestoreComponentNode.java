package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class RestoreComponentNode extends DynSemNode {

	private final FrameSlot componentSlot;
	@Child private MatchPattern patt;

	public RestoreComponentNode(SourceSection source, FrameSlot compSlot, MatchPattern patt) {
		super(source);
		this.componentSlot = compSlot;
		this.patt = patt;
	}

	public void executeRestore(VirtualFrame frame, VirtualFrame components) {
		patt.executeMatch(frame, components.getValue(componentSlot));
	}

}
