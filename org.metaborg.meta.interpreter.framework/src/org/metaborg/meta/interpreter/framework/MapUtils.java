package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import com.github.krukow.clj_ds.PersistentMap;

public class MapUtils {

	public static <K, V> PersistentMap<K, V> plus(PersistentMap<K, V> one,
			PersistentMap<K, V> other) {
		PersistentMap<K, V> nmap = one;
		for (K key : other.keySet()) {
			nmap = nmap.plus(key, other.get(key));
		}
		return nmap;
	}
	
}
