package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

// TODO occurrences of this node should be replaced with constructor specific logic for accessing a field in a constant constructor.
public class ChildAccessTermBuild extends TermBuild {

	@Child private TermBuild termNode;

	private final int childIdx;

	public ChildAccessTermBuild(TermBuild termNode, int childIdx, SourceSection source) {
		super(source);
		this.termNode = termNode;
		this.childIdx = childIdx;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return BuiltinTypesGen.asITerm(termNode.executeGeneric(frame)).allSubterms()[childIdx];
	}

}
