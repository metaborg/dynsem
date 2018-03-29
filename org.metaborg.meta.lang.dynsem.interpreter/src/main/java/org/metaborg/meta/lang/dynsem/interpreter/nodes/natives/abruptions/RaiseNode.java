package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeOperationNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class RaiseNode extends NativeOperationNode {

	@Child private TermBuild thrownBuildNode;

	public RaiseNode(SourceSection source) {
		super(source);
	}

	@Override
	public RuleResult execute(VirtualFrame frame, VirtualFrame components) {
		throw new AbortedEvaluationException(thrownBuildNode.executeGeneric(frame),
				components.materialize());
	}

}
