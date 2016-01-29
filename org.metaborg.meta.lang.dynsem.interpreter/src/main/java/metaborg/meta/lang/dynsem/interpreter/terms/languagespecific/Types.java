package metaborg.meta.lang.dynsem.interpreter.terms.languagespecific;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem({ PlusTerm.class, IExprTerm.class, ITerm.class, int.class })
public class Types {

}
