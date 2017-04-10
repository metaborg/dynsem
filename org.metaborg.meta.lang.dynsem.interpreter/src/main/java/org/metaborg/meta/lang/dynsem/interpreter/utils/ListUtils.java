package org.metaborg.meta.lang.dynsem.interpreter.utils;

import java.util.LinkedList;

import com.github.krukow.clj_lang.IPersistentStack;
import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class ListUtils {

	@TruffleBoundary
	public static <T> IPersistentStack<T> reverse(IPersistentStack<T> list) {
		final PersistentList<T> plist = (PersistentList<T>) list;

		final LinkedList<T> temp = new LinkedList<>();
		for (T t : plist) {
			temp.add(t);
		}
		return PersistentList.create(temp);
	}

}
