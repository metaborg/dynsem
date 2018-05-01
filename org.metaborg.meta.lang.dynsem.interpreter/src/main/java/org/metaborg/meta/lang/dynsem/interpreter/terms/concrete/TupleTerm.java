package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.ITupleTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IWithStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public final class TupleTerm implements ITupleTerm, IWithStrategoTerm {

	private final int arity;
	private final String sort;

	@CompilationFinal(dimensions = 1) private final Object[] subterms;
	private final IStrategoTerm strategoTerm;

	public TupleTerm(String sort, Object[] subterms, IStrategoTerm strTerm) {
		this.sort = sort;
		this.arity = subterms.length;
		this.subterms = subterms;
		this.strategoTerm = strTerm;
	}

	public TupleTerm(String sort, Object[] subterms) {
		this(sort, subterms, null);
	}

	@Override
	public String sort() {
		return sort;
	}

	@Override
	public int size() {
		return arity;
	}

	@Override
	public Object[] subterms() {
		return subterms;
	}

	@Override
	public Object subterm(int idx) {
		return subterm(idx);
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
	public String dispatchkey() {
		return sort();
	}

}
