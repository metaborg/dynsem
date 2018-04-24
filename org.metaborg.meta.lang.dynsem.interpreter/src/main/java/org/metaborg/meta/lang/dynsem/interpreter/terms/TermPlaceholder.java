package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class TermPlaceholder implements ITerm {

	public static final TermPlaceholder INSTANCE = new TermPlaceholder();

	private TermPlaceholder() {

	}

	@Override
	@TruffleBoundary
	public int size() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

	@Override
	@TruffleBoundary
	public ITermInstanceChecker getCheck() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

	@Override
	@TruffleBoundary
	public boolean hasStrategoTerm() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

	@Override
	@TruffleBoundary
	public IStrategoTerm getStrategoTerm() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

}
