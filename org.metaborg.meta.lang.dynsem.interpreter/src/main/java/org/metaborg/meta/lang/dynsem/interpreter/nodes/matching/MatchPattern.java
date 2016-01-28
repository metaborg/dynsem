package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MatchPattern extends Node {

	public MatchPattern(SourceSection source) {
		super(source);
	}
	
	public abstract boolean execute(IStrategoTerm term, VirtualFrame frame);
}
