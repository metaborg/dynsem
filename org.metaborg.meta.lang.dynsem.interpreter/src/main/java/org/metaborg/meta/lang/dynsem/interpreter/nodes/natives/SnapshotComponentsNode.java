package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class SnapshotComponentsNode extends DynSemNode {

	private final FrameDescriptor snapshotDescriptor;
	
	public SnapshotComponentsNode(SourceSection source, FrameDescriptor componentLabelsDescriptor) {
		super(source);
		this.snapshotDescriptor = componentLabelsDescriptor;
	}
	
	public MaterializedFrame execute(VirtualFrame frame) {
		MaterializedFrame f = Truffle.getRuntime().createMaterializedFrame(null, snapshotDescriptor);
		// TODO 
		throw new RuntimeException("Not implemented");
	}
}
