package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NativeCallPremise extends Premise {
	@Child private NativeExecutableNode execNode;

	@Child private MatchPattern rhsNode;

	@Children private final MatchPattern[] rhsRwNodes;

	public NativeCallPremise(SourceSection source, NativeExecutableNode execNode, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes) {
		super(source);
		this.execNode = execNode;
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
	}

	@Specialization
	@ExplodeLoop
	public void doExecute(VirtualFrame frame) {
		final RuleResult res = execNode.execute(frame);

		rhsNode.executeMatch(frame, res.result);

		// evaluate the RHS component pattern matches
		final Object[] components = res.components;
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i].executeMatch(frame, components[i]);
		}
	}


}
