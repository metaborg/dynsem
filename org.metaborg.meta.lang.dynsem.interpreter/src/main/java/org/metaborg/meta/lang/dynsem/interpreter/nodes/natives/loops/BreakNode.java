package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeExecutableNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class BreakNode extends NativeExecutableNode {

	@Child private TermBuild valBuildnode;

	@Children private final TermBuild[] rwCompNodes;

	public BreakNode(SourceSection source, TermBuild valBuildNode, TermBuild[] rwCompNodes) {
		super(source);
		this.valBuildnode = valBuildNode;
		this.rwCompNodes = rwCompNodes;
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object val = valBuildnode.executeGeneric(frame);

		Object[] components = new Object[rwCompNodes.length];
		for (int i = 0; i < rwCompNodes.length; i++) {
			components[i] = rwCompNodes[i].executeGeneric(frame);
		}
		throw new LoopBreakException(val, components);
	}



}
