package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.source.SourceSection;

//@NodeChildren({@NodeChild(value="t", t))
public abstract class LookupNode extends DynSemNode {

	public LookupNode(SourceSection source) {
		super(source);
	}

	public abstract Location execute(VirtualFrame frame);

}
