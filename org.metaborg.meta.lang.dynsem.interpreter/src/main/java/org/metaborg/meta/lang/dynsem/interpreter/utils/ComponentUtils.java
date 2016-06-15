package org.metaborg.meta.lang.dynsem.interpreter.utils;

import java.util.Objects;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;

public final class ComponentUtils {

	public static void setComponent(Object[] arguments, int idx, Object value) {
		if (DynSemContext.LANGUAGE.isSafeComponentsEnabled()) {
			arguments[idx] = Objects.requireNonNull(value, "Attempted to write null component at index " + idx);
		} else {
			arguments[idx] = value;
		}
	}

	public static Object getComponent(Object[] arguments, int idx) {
		if (DynSemContext.LANGUAGE.isSafeComponentsEnabled()) {
			return Objects.requireNonNull(arguments[idx], "Attempted access to null component at index " + idx);
		} else {
			return arguments[idx];
		}
	}
}
