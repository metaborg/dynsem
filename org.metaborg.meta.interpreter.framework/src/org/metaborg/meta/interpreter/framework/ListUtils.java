package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class ListUtils {

	public static IStrategoList toStrategoTerm(INodeList list,
			ITermFactory factory) {
		if (list.isEmpty()) {
			return factory.makeList();
		}
		Object head = list.head();
		IStrategoTerm headTerm = null;
		if (head instanceof IConvertibleToStrategoTerm) {
			headTerm = ((IConvertibleToStrategoTerm) head)
					.toStrategoTerm(factory);
		} else if (head instanceof String) {
			headTerm = factory.makeString((String) head);
		} else if (head instanceof Integer) {
			headTerm = factory.makeInt((Integer) head);
		} else if (head instanceof Double) {
			headTerm = factory.makeReal((Double) head);
		} else {
			throw new RuntimeException("Unsupported list element: " + head);
		}

		return factory.makeListCons(headTerm,
				toStrategoTerm(list.tail(), factory));
	}

	public static int hashCode(INodeList list) {
		final int prime = 31;
		final Object head = list.head();
		final INodeList tail = list.tail();
		int result = 1;
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + list.size();
		result = prime * result + ((tail == null) ? 0 : tail.hashCode());
		return result;
	}

	public static boolean equals(INodeList a, Object b) {
		if(a == b)
			return true;
		if(b == null)
			return false;
		if((b instanceof INodeList) && a.isEmpty() && ((INodeList) b).isEmpty())
			return true;
		if (a.getClass() != b.getClass())
			return false;
		INodeList other = (INodeList) b;
		if (a.size() != other.size()) {
			return false;
		}
		if (a.head() == null) {
			if (other.head() != null)
				return false;
		} else if (!a.head().equals(other.head()))
			return false;
		if (a.size() != other.size())
			return false;
		if (a.tail() == null) {
			if (other.tail() != null)
				return false;
		} else if (!a.tail().equals(other.tail()))
			return false;
		return true;
	}

	public static String toString(INodeList list) {
		StringBuilder sb = new StringBuilder("[");
		while (list.size() > 0) {
			sb.append(list.head().toString());
			sb.append(", ");
			list = list.tail();
		}
		sb.append(" ]");
		return sb.toString();
	}
}
