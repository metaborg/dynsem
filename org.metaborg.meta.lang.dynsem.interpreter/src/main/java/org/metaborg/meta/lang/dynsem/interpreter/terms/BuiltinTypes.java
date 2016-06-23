package org.metaborg.meta.lang.dynsem.interpreter.terms;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem({ IListTerm.class, IApplTerm.class, ITerm.class, PersistentMap.class, String.class, int.class,
		boolean.class, Object[].class })
public class BuiltinTypes {

}
