package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeOperationNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.SnapshotComponentsNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "snapshotCompsNode", type = SnapshotComponentsNode.class),
		@NodeChild(value = "thrownBuildNode", type = TermBuild.class) })
public abstract class RaiseNode extends NativeOperationNode {

	public RaiseNode(SourceSection source) {
		super(source);
	}

	@Specialization
	public RuleResult execute(MaterializedFrame components, IApplTerm thrown) {
		throw new AbortedEvaluationException(thrown, components);
	}

}
