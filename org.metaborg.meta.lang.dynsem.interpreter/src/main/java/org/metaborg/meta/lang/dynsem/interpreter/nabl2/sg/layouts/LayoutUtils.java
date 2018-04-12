package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.Layout;

public final class LayoutUtils {

	private LayoutUtils() {
	}

	public static Layout getScopeEntryLayout() {
		return ScopeEntryLayoutImpl.LAYOUT;
	}

}
