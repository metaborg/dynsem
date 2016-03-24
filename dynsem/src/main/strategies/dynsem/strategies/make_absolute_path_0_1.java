package dynsem.strategies;

import java.io.File;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class make_absolute_path_0_1 extends Strategy {

	public static make_absolute_path_0_1 instance = new make_absolute_path_0_1();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm relpath,
			IStrategoTerm projectpath) {

		if (!(relpath instanceof IStrategoString)) {
			return null;
		}
		if (!(projectpath instanceof IStrategoString)) {
			return null;
		}

		if (!new File(Tools.javaString(relpath)).isAbsolute()) {
			return context.getFactory().makeString(
					new File(Tools.javaString(projectpath), Tools
							.javaString(relpath)).getAbsolutePath());
		}
		
		return relpath;
	}

}
