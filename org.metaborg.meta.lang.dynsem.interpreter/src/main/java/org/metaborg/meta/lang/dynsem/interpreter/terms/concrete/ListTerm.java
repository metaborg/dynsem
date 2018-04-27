package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IWithStrategoTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.ListUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.github.krukow.clj_lang.IPersistentStack;
import com.github.krukow.clj_lang.ISeq;
import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

@SuppressWarnings("rawtypes")
public final class ListTerm implements IListTerm, IWithStrategoTerm {
	private final IPersistentStack backend;

	private final String sort;

	private final IStrategoTerm strategoTerm;

	@TruffleBoundary
	public ListTerm(String sort, Object[] elems, IStrategoTerm strTerm) {
		this(sort, (IPersistentStack) PersistentList.create(elems), strTerm);

	}

	@TruffleBoundary
	public ListTerm(String sort, IStrategoTerm strTerm) {
		this(sort, PersistentList.EMPTY, strTerm);
	}

	private ListTerm(String sort, IPersistentStack backend, IStrategoTerm strTerm) {
		this.sort = sort;
		this.backend = backend;
		this.strategoTerm = strTerm;
	}

	@Override
	@TruffleBoundary
	public int size() {
		return backend.count();
	}

	@Override
	@TruffleBoundary
	public Object subterm(int idx) {
		ISeq seq = backend.seq();
		for (int k = 0; k < idx; k++) {
			seq = seq.next();
		}
		return seq != null ? seq.first() : null;
	}

	@Override
	@TruffleBoundary
	public Object[] subterms() {
		Object[] res = new Object[size()];
		if (res.length > 0) {
			((PersistentList) this.backend).toArray(res);
		}

		return res;
	}

	@Override
	@TruffleBoundary
	public Object head() {
		return backend.peek();
	}

	@Override
	public ListTerm tail() {
		return new ListTerm(sort, _pop(backend), getStrategoTerm());
	}

	@TruffleBoundary
	private static IPersistentStack _pop(IPersistentStack backend) {
		return backend.pop();
	}

	@TruffleBoundary
	private static Object _peek(IPersistentStack backend) {
		return backend.peek();
	}

	@TruffleBoundary
	private static IPersistentStack _cons(IPersistentStack backend, Object elem) {
		return (IPersistentStack) backend.cons(elem);
	}

	@Override
	public ListTerm drop(int n) {
		IPersistentStack tip = backend;
		for (int i = 0; i < n; i++) {
			tip = _pop(tip);
		}
		return new ListTerm(sort, tip, getStrategoTerm());
	}

	@Override
	public Object[] take(int n) {
		Object[] res = new Object[n];
		IPersistentStack tip = backend;
		for (int i = 0; i < n; i++) {
			res[i] = _peek(tip);
			tip = _pop(tip);
		}
		return res;
	}

	@Override
	public ListTerm prefix(Object[] elems) {
		IPersistentStack backend = this.backend;
		for (int idx = elems.length - 1; idx >= 0; idx--) {
			backend = _cons(backend, elems[idx]);
		}
		return new ListTerm(sort, backend, getStrategoTerm());
	}

	@Override
	public ListTerm reverse() {
		return new ListTerm(sort, ListUtils.reverse(backend), getStrategoTerm());
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

}
