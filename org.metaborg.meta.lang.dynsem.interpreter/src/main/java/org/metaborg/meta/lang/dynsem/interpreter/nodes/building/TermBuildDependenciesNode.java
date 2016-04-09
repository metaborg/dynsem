package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;

public class TermBuildDependenciesNode extends Node {

	@Children private final TermBuildStabilityNode[] stabilityNodes;

	public static TermBuildDependenciesNode create(TermBuild[] dependencies) {
		TermBuildStabilityNode[] stabilityNodes = new TermBuildStabilityNode[dependencies.length];
		for (int i = 0; i < stabilityNodes.length; i++) {
			stabilityNodes[i] = TermBuildStabilityNode.create(NodeUtil.cloneNode(dependencies[i]));
		}
		return new TermBuildDependenciesNode(stabilityNodes);
	}

	public TermBuildDependenciesNode(TermBuildStabilityNode[] stabilityNodes) {
		this.stabilityNodes = stabilityNodes;
	}

	@ExplodeLoop
	public void execute(VirtualFrame frame) {
		for (int i = 0; i < stabilityNodes.length; i++) {
			stabilityNodes[i].execute(frame);
		}
	}

}
