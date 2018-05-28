package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import com.oracle.truffle.api.object.Layout;

public final class FrameLayoutUtil {

	public static Layout layout() {
		return FrameLayoutImpl.LAYOUT;
	}
}
