package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

public final class DispatchUtils {

	private DispatchUtils() {
	}

	public static Class<?> nextDispatchClass(Object input, Class<?> fallbackOfClass) {
		Class<?> classOfInput = input.getClass();
		if (IApplTerm.class.isAssignableFrom(classOfInput)) {
			if (classOfInput == fallbackOfClass) {
				return IApplTerm.class.cast(input).getSortClass();
			}
		}
		return null;
	}

	public static Class<?> nextDispatchClass(Object[] args, Class<?> fallbackOfClass) {
		return nextDispatchClass(args[0], fallbackOfClass);
	}
}
