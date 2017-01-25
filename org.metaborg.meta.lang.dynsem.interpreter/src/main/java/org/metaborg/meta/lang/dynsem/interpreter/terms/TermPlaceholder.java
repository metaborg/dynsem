package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class TermPlaceholder implements ITerm {

	public static final TermPlaceholder INSTANCE = new TermPlaceholder();

	private TermPlaceholder() {

	}

	@Override
	public int size() {
		throw new ReductionFailure("May not query properties of the ??? (placeholder) term",
				InterpreterUtils.createStacktrace());
	}

	@Override
	public ITermInstanceChecker getCheck() {
		throw new ReductionFailure("May not query properties of the ??? (placeholder) term",
				InterpreterUtils.createStacktrace());
	}

	@Override
	public boolean hasStrategoTerm() {
		throw new ReductionFailure("May not query properties of the ??? (placeholder) term",
				InterpreterUtils.createStacktrace());
	}

	@Override
	public IStrategoTerm getStrategoTerm() {
		throw new ReductionFailure("May not query properties of the ??? (placeholder) term",
				InterpreterUtils.createStacktrace());
	}

}
