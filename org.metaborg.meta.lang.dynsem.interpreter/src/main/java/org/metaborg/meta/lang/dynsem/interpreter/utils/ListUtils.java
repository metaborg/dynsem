package org.metaborg.meta.lang.dynsem.interpreter.utils;

import java.util.ArrayList;
import java.util.Collections;

import com.github.krukow.clj_lang.IPersistentStack;
import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class ListUtils {

	@TruffleBoundary
	public static <T> IPersistentStack<T> reverse(IPersistentStack<T> list) {
		if (list.count() == 0) {
			return list;
		}

		final ArrayList<T> mlist = new ArrayList<T>((PersistentList<T>) list);

		Collections.reverse(mlist);

		return PersistentList.create(mlist);
	}

}
