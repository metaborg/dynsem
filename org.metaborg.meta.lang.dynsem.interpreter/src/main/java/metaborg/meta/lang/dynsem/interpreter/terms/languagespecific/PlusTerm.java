package metaborg.meta.lang.dynsem.interpreter.terms.languagespecific;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public class PlusTerm implements IExprTerm {

	private final IExprTerm e1, e2;

	public final static String constructor = "Plus";
	public final static int arity = 2;

	public PlusTerm(IExprTerm e1, IExprTerm e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	public IExprTerm get1() {
		return e1;
	}

	public IExprTerm get2() {
		return e2;
	}

	@Override
	public String constructor() {
		return constructor;
	}

	@Override
	public int arity() {
		return arity;
	}

	@Override
	public ITerm[] allSubterms() {
		return new ITerm[] { e1, e2 };
	}

}
