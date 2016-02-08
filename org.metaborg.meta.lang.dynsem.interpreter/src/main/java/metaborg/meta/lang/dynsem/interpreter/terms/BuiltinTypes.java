package metaborg.meta.lang.dynsem.interpreter.terms;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem({ IConTerm.class, ITerm.class, PersistentMap.class, String.class, int.class, boolean.class })
public class BuiltinTypes {

}
