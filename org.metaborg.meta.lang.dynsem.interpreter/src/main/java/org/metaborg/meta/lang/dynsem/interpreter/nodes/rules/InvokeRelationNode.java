package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class InvokeRelationNode extends DynSemNode {
	@Child protected RelationPremiseInputBuilder inputBuilder;

	@Child protected DispatchNode dispatchNode;

	public InvokeRelationNode(SourceSection source, RelationPremiseInputBuilder inputBuilder,
			DispatchNode dispatchNode) {
		super(source);
		this.inputBuilder = inputBuilder;
		this.dispatchNode = dispatchNode;
	}

	public RuleResult execute(VirtualFrame frame) {
		Object[] args = inputBuilder.executeObjectArray(frame);
		return dispatchNode.execute(BuiltinTypesGen.asITerm(args[0]).dispatchkey(), args);
	}
}
