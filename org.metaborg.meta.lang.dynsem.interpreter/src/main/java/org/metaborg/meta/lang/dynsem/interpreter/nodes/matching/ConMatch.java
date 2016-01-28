package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class ConMatch extends MatchPattern {

	private final String name;
	@Children private final MatchPattern[] children;

	public ConMatch(String name, MatchPattern[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	@Override
	@ExplodeLoop
	public boolean execute(IStrategoTerm term, VirtualFrame frame) {
		if (!Tools.isTermAppl(term)) {
			return false;
		}

		IStrategoAppl appl = (IStrategoAppl) term;
		if (!appl.getConstructor().getName().equals(name)) {
			return false;
		}

		IStrategoTerm[] kids = appl.getAllSubterms();
		if (kids.length != children.length) {
			return false;
		}
		for (int i = 0; i < children.length; i++) {
			if (!children[i].execute(kids[i], frame)) {
				return false;
			}
		}

		return true;
	}

}
