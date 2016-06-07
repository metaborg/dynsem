package org.metaborg.meta.lang.dynsem.interpreter.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class MapUtils {

	@TruffleBoundary
	public static <K, V> PersistentMap<K, V> plus(PersistentMap<K, V> one, PersistentMap<K, V> other) {
		TransientMap<K, V> tmp = ((PersistentHashMap) one).asTransient();

		Iterator<Map.Entry<K, V>> it = (Iterator<Entry<K, V>>) ((PersistentHashMap) other).entrySet().iterator();
		while (it.hasNext()) {
			Entry<K, V> tuple = it.next();
			tmp.plus(tuple.getKey(), tuple.getValue());
		}

		return tmp.persist();
	}

	@TruffleBoundary
	public static <K, V> PersistentMap<K, V> add(PersistentMap<K, V> map, K key, V val) {

		return map.plus(key, val);

	}

	@TruffleBoundary
	public static <K, V> V get(PersistentMap<K, V> map, K key) {
		return map.get(key);
	}

}
