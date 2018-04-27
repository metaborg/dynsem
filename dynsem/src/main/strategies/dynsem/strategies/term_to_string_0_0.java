package dynsem.strategies;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class term_to_string_0_0 extends Strategy {
	public static term_to_string_0_0 instance = new term_to_string_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		return context.getFactory().makeString(current.toString());
	}
}
