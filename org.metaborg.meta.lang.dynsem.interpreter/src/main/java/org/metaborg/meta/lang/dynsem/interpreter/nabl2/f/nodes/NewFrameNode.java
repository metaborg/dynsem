package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NewFrameNode extends DynSemNode {

	public NewFrameNode(SourceSection source) {
		super(source);
	}

	public abstract DynamicObject execute(VirtualFrame frame);

}
