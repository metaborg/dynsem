package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.frame.VirtualFrame;

@Deprecated
public class TermBuildCacheOptionNode extends TermBuild {

	@Child private TermBuild buildNode;

	public TermBuildCacheOptionNode(TermBuild buildNode) {
		super(buildNode.getSourceSection());
		this.buildNode = buildNode;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		if (getContext().isTermCachingEnabled()) {
			return replace(TermBuildCacheNodeGen.create(buildNode)).executeGeneric(frame);
		}

		return replace(buildNode).executeGeneric(frame);
	}

}
