package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class IntPattern extends MatchPattern {

	private final int value;

	public IntPattern(int value, SourceSection source) {
		super(source);
		this.value = value;
	}

	@Override
	public boolean execute(IStrategoTerm term, VirtualFrame frame) {
		if (Tools.isTermInt(term) && Tools.asJavaInt(term) == value) {
			return true;
		}
		return false;
	}
}
