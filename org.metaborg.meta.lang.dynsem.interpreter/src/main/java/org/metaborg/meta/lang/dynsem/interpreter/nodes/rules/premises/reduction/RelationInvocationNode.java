package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class RelationInvocationNode extends DynSemNode {
	@Child protected RelationPremiseInputBuilder inputBuilder;

	@Child protected RelationDispatch dispatchNode;

	public RelationInvocationNode(RelationPremiseInputBuilder inputBuilder, RelationDispatch dispatchNode,
			SourceSection source) {
		super(source);
		this.inputBuilder = inputBuilder;
		this.dispatchNode = dispatchNode;
	}

	public RuleResult execute(VirtualFrame frame) {
		Object[] args = inputBuilder.executeObjectArray(frame);
		return dispatchNode.execute(frame, args);
	}
}
