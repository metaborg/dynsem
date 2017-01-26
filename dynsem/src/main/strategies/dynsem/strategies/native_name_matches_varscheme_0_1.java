package dynsem.strategies;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class native_name_matches_varscheme_0_1 extends Strategy {

	public static native_name_matches_varscheme_0_1 instance = new native_name_matches_varscheme_0_1();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm usedWordTerm, IStrategoTerm schemeNameTerm) {

		if (usedWordTerm instanceof IStrategoString) {
			if (schemeNameTerm instanceof IStrategoString) {
				final String schemeName = Tools.asJavaString(schemeNameTerm);
				final String usedWord = Tools.asJavaString(usedWordTerm);
				final String regex = schemeName + "(_.+|[0-9]+'*|'+[0-9]*)";
				if (usedWord.matches(regex)) {
					return usedWordTerm;
				}
			}
		}
		return null;

	}

}
