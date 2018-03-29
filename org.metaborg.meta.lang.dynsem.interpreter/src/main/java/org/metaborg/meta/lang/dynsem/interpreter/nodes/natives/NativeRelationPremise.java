package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class NativeRelationPremise extends Premise {

	@Child private NativeOperationNode nativeCallNode;

	@Child private SnapshotComponentsNode snapshotComponents;

	@Child private MatchPattern rhsNode;

	@Child private RestoreComponentSnapshotNode restoreComponents;

	public NativeRelationPremise(SourceSection source, NativeOperationNode nativeCallNode, MatchPattern rhsNode) {
		super(source);
		this.nativeCallNode = nativeCallNode;
		this.rhsNode = rhsNode;
	}

	public void execute(VirtualFrame frame) {
		final VirtualFrame components = snapshotComponents.execute(frame);
		// the call will probably update the components in the components frame above
		final Object callResult = nativeCallNode.execute(frame, components);

		// evaluate the RHS pattern match
		rhsNode.executeMatch(frame, callResult);

		// update component variables from the component frame
		restoreComponents.executeRestore(frame, components);
	}

	
}
