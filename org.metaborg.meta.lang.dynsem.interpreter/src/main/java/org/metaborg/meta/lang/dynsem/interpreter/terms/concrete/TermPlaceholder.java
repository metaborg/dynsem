package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IWithStrategoTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class TermPlaceholder implements ITerm, IWithStrategoTerm {

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

	@Override
	public Object subterm(int idx) {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

	@Override
	public Object[] subterms() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

	@Override
	public String sort() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

	@Override
	public String dispatchkey() {
		throw new RuntimeException(
				"May not query properties of the ??? (placeholder) term" + InterpreterUtils.createStacktrace());
	}

}
