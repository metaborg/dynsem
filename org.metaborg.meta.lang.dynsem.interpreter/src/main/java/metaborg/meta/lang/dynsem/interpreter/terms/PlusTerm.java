package metaborg.meta.lang.dynsem.interpreter.terms;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public class PlusTerm implements IExprTerm {

	private IExprTerm e1, e2;

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

}
