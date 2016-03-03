package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public class ConReductionPremiseLHS extends Node {

	@Child protected TermBuild termNode;
	@Children protected final TermBuild[] roNodes;
	@Children protected final TermBuild[] rwNodes;

	public ConReductionPremiseLHS(TermBuild termNode, TermBuild[] roNodes,
			TermBuild[] rwNodes, SourceSection source) {
		super(source);
		this.termNode = termNode;
		this.roNodes = roNodes;
		this.rwNodes = rwNodes;
	}

	@ExplodeLoop
	public Object[] executeObjectArray(VirtualFrame frame) {
		IConTerm term;
		try {
			term = termNode.executeIConTerm(frame);
		} catch (UnexpectedResultException e) {
			throw new RuntimeException("Unexpected reduction lhs", e);
		}

		int arity = term.arity();
		int offset = 1 + arity;
		Object[] args = new Object[offset + roNodes.length + rwNodes.length];
		args[0] = term;
		System.arraycopy(term.allSubterms(), 0, args, 1, arity);

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
