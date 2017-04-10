package org.metaborg.meta.lang.dynsem.interpreter.utils;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class ListUtils {
	
	@TruffleBoundary
	public static <T> IPersistentStack<T> reverse(IPersistentStack<T> list) {
		throw new RuntimeException("List reversal not implemented");
	}

}
