package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeExecutableNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RaiseNode extends NativeExecutableNode {

	@Children private final TermBuild[] rwCompBuildNodes;

	@Child private TermBuild thrownBuildNode;

	public RaiseNode(SourceSection source, TermBuild[] rwCompBuildNodes,
			TermBuild thrownBuildNode) {
		super(source);
		this.rwCompBuildNodes = rwCompBuildNodes;
		this.thrownBuildNode = thrownBuildNode;
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object thrownT = thrownBuildNode.executeGeneric(frame);
		Object[] rwCompsT = new Object[rwCompBuildNodes.length];
		for (int i = 0; i < rwCompBuildNodes.length; i++) {
			rwCompsT[i] = rwCompBuildNodes[i].executeGeneric(frame);
		}
		throw new AbortedEvaluationException(thrownT, rwCompsT);
	}



}
