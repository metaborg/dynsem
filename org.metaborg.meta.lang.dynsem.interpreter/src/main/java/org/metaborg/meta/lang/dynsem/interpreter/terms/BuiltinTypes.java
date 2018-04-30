package org.metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ConsNilList;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.TupleTerm;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.TypeSystem;

// FIXME: extend the classes here with SG-FRAMES-ARRAY classes
@TypeSystem({ ApplTerm.class, TupleTerm.class, ITupleTerm.class, Cons.class, Nil.class, ConsNilList.class,
		IListTerm.class, IApplTerm.class, ITerm.class, PersistentMap.class, String.class, int.class, boolean.class,
		Object[].class })
public class BuiltinTypes {

}
