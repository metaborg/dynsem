package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild(value = "tb", type = TermBuild.class)
public abstract class TypeCastNode extends TermBuild {

	public TypeCastNode() {
		super(SourceUtils.dynsemSourceSectionUnvailable());
	}

	public abstract Object executeCast(VirtualFrame frame);

	public abstract Object executeEvaluated(Object term);

}
