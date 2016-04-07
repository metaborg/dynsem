package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

// TODO occurrences of this node should be replaced with constructor specific logic for accessing a field in a constant constructor.
public class ChildAccessTermBuild extends TermBuild {

	private final int childIdx;

	public ChildAccessTermBuild(int childIdx, SourceSection source) {
		super(source);
		this.childIdx = childIdx;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		ITerm term = BuiltinTypesGen.asITerm(frame.getArguments()[0]);
		return term.allSubterms()[childIdx];
	}

}