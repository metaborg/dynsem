package dynsem.strategies;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class pkg_of_0_0 extends Strategy {
	public static pkg_of_0_0 instance = new pkg_of_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		final String str = Tools.asJavaString(current);
		
		return context.getFactory().makeString(str.substring(0,str.lastIndexOf(".")));
	}
}
