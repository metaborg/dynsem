package org.metaborg.meta.interpreter.framework;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class MapUtils {

	@TruffleBoundary
	public static <K, V> PersistentMap<K, V> plus(PersistentMap<K, V> one,
			PersistentMap<K, V> other) {
		PersistentMap<K, V> nmap = one;
		for (K key : other.keySet()) {
			nmap = nmap.plus(key, other.get(key));
		}
		return nmap;
	}

	@TruffleBoundary
	@SuppressWarnings("unchecked")
	public static <K, V> PersistentMap<K, V> add(PersistentMap<K, V> map,
			K key, V val) {

		return map.plus(
				(K) (key instanceof IGenericNode ? ((IGenericNode) key)
						.specialize() : key),
				(V) (val instanceof IGenericNode ? ((IGenericNode) val)
						.specialize() : val));

	}

	@TruffleBoundary
	public static <K, V> V get(PersistentMap<K, V> map, K key) {
		return map.get(key instanceof IGenericNode ? ((IGenericNode) key)
				.specialize() : key);
	}

}
