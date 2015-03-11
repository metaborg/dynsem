package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public abstract class AbstractPrimitiveList<T> implements INodeList {
	T head;

	public AbstractPrimitiveList<T> tail;

	public final int size;

	public AbstractPrimitiveList() {
		this(null, null);
	}

	public AbstractPrimitiveList(T head, AbstractPrimitiveList<T> tail) {
		this.head = head;
		this.tail = tail;
		this.size = (head == null ? 0 : 1) + (tail == null ? 0 : tail.size());
	}

	@Override
	public T head() {
		if (head == null) {
			throw new InterpreterException("No such element exception");
		}
		return head;
	}

	@Override
	public void replaceHead(Object newHead) {
		this.head = (T) newHead;

	}

	@Override
	public AbstractPrimitiveList<T> tail() {
		if (tail == null) {
			throw new InterpreterException("No such element exception");
		}
		return tail;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return head == null;
	}

	@Override
	public int hashCode() {
		return ListUtils.hashCode(this);
	}

	@Override
	public IStrategoTerm toStrategoTerm(ITermFactory factory) {
		return ListUtils.toStrategoTerm(this, factory);
	}

	@Override
	public boolean equals(Object obj) {
		return ListUtils.equals(this, obj);
	}

	@Override
	public String toString() {
		return ListUtils.toString(this);
	}
	
}
