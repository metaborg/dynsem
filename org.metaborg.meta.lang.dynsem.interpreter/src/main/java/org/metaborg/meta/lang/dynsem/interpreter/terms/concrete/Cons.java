package org.metaborg.meta.lang.dynsem.interpreter.terms.concrete;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class Cons extends ConsNilList {

	private final Object head;
	private final IListTerm tail;

	public Cons(String sort, Object head, IListTerm tail, IStrategoTerm aterm) {
		super(sort, aterm);
		this.head = head;
		this.tail = tail;
	}

	@Override
	public int size() {
		return 1 + tail.size();
	}

	@Override
	public Object head() {
		return head;
	}

	@Override
	public IListTerm tail() {
		return tail;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "C(" + head + tail.toString() + ")";
	}

	@Override
	public String dispatchkey() {
		return sort();
	}
}
