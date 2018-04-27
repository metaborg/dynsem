package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.TupleTerm;

public final class DispatchUtils {

	private DispatchUtils() {
	}

	public static String nextDispatchKey(Object term, String fallbackOfKey) {
		if (term instanceof ApplTerm) {
			String sortDispatchKey = ((ApplTerm) term).sort();
			if (sortDispatchKey != null && !sortDispatchKey.equals(fallbackOfKey)) {
				return sortDispatchKey;
			}
		}
		throw new IllegalStateException("No next dispatch key for: " + term);
	}

	public static String nextDispatchKey(Object[] args, String fallbackOfKey) {
		return nextDispatchKey(args[0], fallbackOfKey);
	}

	public static String dispatchKeyOf(Object t) {
		if (t instanceof ApplTerm) {
			return dispatchKeyOf((ApplTerm) t);
		}

		if (t instanceof TupleTerm) {
			return dispatchKeyOf((TupleTerm) t);
		}

		if (t instanceof ListTerm) {
			return dispatchKeyOf((ListTerm) t);
		}

		throw new IllegalStateException("Cannot determine dispatch key for term: " + t);
	}

	public static String dispatchKeyOf(ApplTerm appl) {
		return appl.name() + "/" + appl.size();
	}

	public static String dispatchKeyOf(TupleTerm tupl) {
		return tupl.sort();
	}

	public static String dispatchKeyOf(ListTerm list) {
		return list.sort();
	}
}
