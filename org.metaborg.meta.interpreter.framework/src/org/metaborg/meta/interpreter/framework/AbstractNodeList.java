/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import java.util.NoSuchElementException;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

/**
 * @author vladvergu
 *
 */
public abstract class AbstractNodeList<T extends Node> extends Node implements
		IList<T> {

	@Child private T head;
	@Child private AbstractNodeList<T> tail;

	private final int size;

	public AbstractNodeList(SourceSection src) {
		this(src, null, null);
	}

	public AbstractNodeList(SourceSection src, T head, AbstractNodeList<T> tail) {
		super(src);
		this.head = head;
		this.tail = tail;
		this.size = (head == null ? 0 : 1) + (tail == null ? 0 : tail.size());
	}

	@Override
	public IStrategoList toStrategoTerm(ITermFactory factory) {
		if (size == 0) {
			return factory.makeList();
		}
		IStrategoTerm headTerm = null;
		if (head instanceof IConvertibleToStrategoTerm) {
			headTerm = ((IConvertibleToStrategoTerm) head)
					.toStrategoTerm(factory);
		} else {
			throw new RuntimeException("Unsupported list element: " + head);
		}

		return factory.makeListCons(headTerm, tail.toStrategoTerm(factory));
	}

	@Override
	public T head() {
		if (head == null) {
			throw new NoSuchElementException();
		}
		return head;
	}

	@Override
	public void replaceHead(T newHead) {
		this.head = newHead;
	}

	@Override
	public AbstractNodeList<T> tail() {
		if (tail == null) {
			throw new NoSuchElementException();
		}
		return tail;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + size;
		result = prime * result + ((tail == null) ? 0 : tail.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractNodeList<?> other = (AbstractNodeList<?>) obj;
		if (size != other.size) {
			return false;
		}
		if (head == null) {
			if (other.head != null) {
				return false;
			}
		} else if (!head.equals(other.head)) {
			return false;
		}
		if (tail == null) {
			if (other.tail != null) {
				return false;
			}
		} else if (!tail.equals(other.tail)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "[]";
		}

		return "[" + head + ", " + tail + "]";
	}
}
