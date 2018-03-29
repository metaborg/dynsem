package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NativeOperationNode extends DynSemNode {

	public NativeOperationNode(SourceSection source) {
		super(source);
	}

	public abstract Object execute(VirtualFrame frame, VirtualFrame components);

}
