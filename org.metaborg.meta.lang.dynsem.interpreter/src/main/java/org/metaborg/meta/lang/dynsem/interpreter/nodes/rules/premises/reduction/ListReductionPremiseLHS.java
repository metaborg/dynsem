package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public class ListReductionPremiseLHS extends Node {

	@Child protected TermBuild termNode;
	@Children protected final TermBuild[] roNodes;
	@Children protected final TermBuild[] rwNodes;

	public ListReductionPremiseLHS(TermBuild termNode, TermBuild[] roNodes,
			TermBuild[] rwNodes, SourceSection source) {
		super(source);
		this.termNode = termNode;
		this.roNodes = roNodes;
		this.rwNodes = rwNodes;
	}

	@ExplodeLoop
	public Object[] executeObjectArray(VirtualFrame frame) {
		IPersistentStack<?> term;
		try {
			term = termNode.executeList(frame);
		} catch (UnexpectedResultException e) {
			throw new RuntimeException("Unexpected reduction lhs", e);
		}

		int offset = 1;
		Object[] args = new Object[offset + roNodes.length + rwNodes.length];
		args[0] = term;

		CompilerAsserts.compilationConstant(roNodes.length);
		CompilerAsserts.compilationConstant(rwNodes.length);

		for (int i = 0; i < roNodes.length; i++) {
			args[offset + i] = roNodes[i].executeGeneric(frame);
		}

		offset += roNodes.length;

		for (int i = 0; i < rwNodes.length; i++) {
			args[offset + i] = rwNodes[i].executeGeneric(frame);
		}

		return args;
	}
}
