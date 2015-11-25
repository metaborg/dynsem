package org.metaborg.meta.interpreter.framework;

import java.util.List;
import java.util.NoSuchElementException;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public class L_Double extends Node implements IList<Double> {

	private Double head;
	@Child private L_Double tail;

	private final int size;

	public L_Double(SourceSection src) {
		this(src, null, null);
	}

	public L_Double(SourceSection source, Double head, L_Double tail) {
		super(source);
		this.head = head;
		this.tail = tail;
		this.size = (head == null ? 0 : 1) + (tail == null ? 0 : tail.size());
	}

	@Override
	public Double head() {
		if (head == null) {
			throw new NoSuchElementException();
		}
		return head;
	}

	@Override
	public void replaceHead(Double newHead) {
		this.head = newHead;
	}

	@Override
	public L_Double tail() {
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
	public IStrategoList toStrategoTerm(ITermFactory factory) {
		if (size == 0) {
			return factory.makeList();
		}

		return factory.makeListCons(factory.makeReal(head),
				tail.toStrategoTerm(factory));
	}

	public String toString() {
		return "[" + head + ", " + tail + "]";
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
		L_Double other = (L_Double) obj;
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

	public static L_Double fromStrategoTerm(IStrategoTerm alist) {
		L_Double list = new L_Double(SourceSectionUtil.fromStrategoTerm(alist));
		for (IStrategoTerm elem : alist) {
			final SourceSection src = SourceSectionUtil.fromStrategoTerm(elem);
			list = new L_Double(src, Tools.asJavaDouble(elem), list);
		}
		return list;
	}

	@TruffleBoundary
	public static L_Double fromList(List<Double> l) {
		final List<Double> revL = Lists.reverse(l);
		final SourceSection ss = SourceSectionUtil.none();
		L_Double ll = new L_Double(ss);
		for (Double a : revL) {
			ll = new L_Double(ss, a, ll);
		}
		return ll;
	}
}
