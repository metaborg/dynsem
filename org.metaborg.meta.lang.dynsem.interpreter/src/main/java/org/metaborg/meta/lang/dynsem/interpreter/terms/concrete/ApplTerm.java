package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IWithStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class ApplTerm implements IApplTerm, IWithStrategoTerm {

	private final String sort;
	private final String name;
	private final int arity;
	private final String dispatchKey;

	@CompilationFinal(dimensions = 1) private final Object[] subterms;

	private final IStrategoTerm strategoTerm;

	public ApplTerm(String sort, String name, Object[] subterms, String dispatchKey,
			IStrategoTerm strTerm) {
		this.sort = sort;
		this.name = name;
		this.arity = subterms.length;
		this.subterms = subterms;
		this.strategoTerm = strTerm;
		this.dispatchKey = dispatchKey;
	}

	public ApplTerm(String sort, String name, Object[] subterms, String dispatchKey) {
		this(sort, name, subterms, dispatchKey, null);
	}

	@Override
	public int size() {
		return arity;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Object[] subterms() {
		return subterms;
	}

	@Override
	public Object subterm(int idx) {
		return subterms[idx];
	}

	@Override
	public String sort() {
		return sort;
	}

	@Override
	public boolean hasStrategoTerm() {
		return strategoTerm != null;
	}

	@Override
	public IStrategoTerm getStrategoTerm() {
		return strategoTerm;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		if (strategoTerm != null) {
			return strategoTerm.toString();
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(");
		for (int i = 0; i < arity; i++) {
			sb.append(subterms[i]);
			if (i < arity - 1) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String dispatchkey() {
		return dispatchKey;
	}

}
