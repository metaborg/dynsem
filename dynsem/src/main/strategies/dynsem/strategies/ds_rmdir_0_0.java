package dynsem.strategies;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class ds_rmdir_0_0 extends Strategy {

	public static ds_rmdir_0_0 instance = new ds_rmdir_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {

		if (!(current instanceof IStrategoString)) {
			return null;
		}

		boolean success = context.getIOAgent().rmdir(Tools.javaString(current));

		return success ? current : null;
	}

}
