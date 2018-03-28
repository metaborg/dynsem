package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class NativeRelationPremise extends Premise {

	@Child private NativeOperationNode nativeCallNode;

	@Child private MatchPattern rhsNode;
	
	@Children private final MatchPattern[] rhsRwNodes;

	public NativeRelationPremise(SourceSection source, NativeOperationNode nativeCallNode,
			MatchPattern rhsNode, MatchPattern[] rhsRwNodes) {
		super(source);
		this.nativeCallNode = nativeCallNode;
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsRwNodes;
	}

	@Override
	@ExplodeLoop
	public void execute(VirtualFrame frame) {
		final RuleResult res = nativeCallNode.execute(frame);
		
		// evaluate the RHS pattern match
		rhsNode.executeMatch(frame, res.result);

		// evaluate the RHS component pattern matches
		final Object[] components = res.components;
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i].executeMatch(frame, InterpreterUtils.getComponent(getContext(), components, i));
		}	
	}

}
