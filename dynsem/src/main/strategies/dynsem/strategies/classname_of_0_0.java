package dynsem.strategies;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class classname_of_0_0 extends Strategy {
	public static classname_of_0_0 instance = new classname_of_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		final String str = Tools.asJavaString(current);

		return context.getFactory().makeString(str.substring(str.lastIndexOf(".") + 1, str.length()));
	}
}
