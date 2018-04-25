package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "tb", type = TermBuild.class)
public abstract class TypeCheckedBuild extends TermBuild {

	public TypeCheckedBuild(SourceSection source) {
		super(source);
	}

	public abstract Object executeLeafCast(VirtualFrame frame);

	public abstract Object executeEvaluated(Object term);

}
