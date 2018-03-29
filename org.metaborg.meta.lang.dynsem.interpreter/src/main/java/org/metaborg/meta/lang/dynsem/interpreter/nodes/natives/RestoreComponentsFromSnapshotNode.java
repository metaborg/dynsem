package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RestoreComponentSnapshotNode extends DynSemNode {

	@Children private final RestoreComponentNode[] restoreNodes;
	
	public RestoreComponentSnapshotNode(SourceSection source, RestoreComponentNode[] restoreNodes) {
		super(source);
		this.restoreNodes = restoreNodes;
	}
	
	@ExplodeLoop
	public void executeRestore(VirtualFrame frame, VirtualFrame components) {
		CompilerAsserts.compilationConstant(restoreNodes.length);
		for(int i = 0; i < restoreNodes.length; i++) {
			restoreNodes[i].executeRestore(frame, components);
		}
	}
}
