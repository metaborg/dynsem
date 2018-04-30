package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IWithStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.nodes.ExplodeLoop;

public abstract class ConsNilList implements IListTerm, IWithStrategoTerm {

	private final String sort;
	private final IStrategoTerm aterm;

	public ConsNilList(String sort, IStrategoTerm aterm) {
		this.sort = sort;
		this.aterm = aterm;

	}

	@Override
	public boolean hasStrategoTerm() {
		return aterm != null;
	}

	@Override
	public IStrategoTerm getStrategoTerm() {
		return aterm;
	}

	@Override
	public IListTerm drop(int n) {
		if (n == 0) {
			return this;
		} else {
			return tail().drop(n - 1);
		}
	}

	@Override
	@ExplodeLoop // FIXME: this operation should be split into separate node
	public Object[] subterms() {
		Object[] subterms = new Object[size()];
		IListTerm tip = this;
		for (int i = 0; i < subterms.length; i++) {
			subterms[i] = tip.head();
			tip = tip.tail();
		}
		return subterms;
	}

	@Override
	public Object[] take(int n) {
		assert n <= size();
		Object[] subterms = new Object[n];
		IListTerm tip = this;
		for (int i = 0; i < subterms.length; i++) {
			subterms[i] = tip.head();
			tip = tip.tail();
		}
		return subterms;
	}

	@Override
	public Object subterm(int idx) {
		if (idx == 0) {
			return head();
		} else {
			return tail().subterm(idx - 1);
		}
	}

	@Override
	public ConsNilList prefix(Object[] elems) {
		ConsNilList tip = this;
		for (int i = elems.length - 1; i >= 0; i--) {
			tip = new Cons(sort, elems[i], tip, null);
		}
		return tip;
	}

	@Override
	public String sort() {
		return sort;
	}

	public static ConsNilList fromArray(String sort, Object[] elems, IStrategoTerm aterm) {
		ConsNilList tip = new Nil(sort, aterm);
		for (int i = elems.length - 1; i >= 0; i--) {
			tip = new Cons(sort, elems[i], tip, aterm);
		}
		return tip;
	}

}
