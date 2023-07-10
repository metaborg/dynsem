package dynsem.strategies;

import java.nio.charset.StandardCharsets;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class shorten_uniqueify_string_0_0 extends Strategy {
	public static shorten_uniqueify_string_0_0 instance = new shorten_uniqueify_string_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		final String str = Tools.asJavaString(current);
		if (str.length() < 20 && !str.contains(".")) {
			return current;
		}

		String[] parts = str.split("\\.");

		String name = parts[parts.length - 1];
		String hash = Tools.asJavaString(context.invokePrimitive("digest", context.getFactory().makeString(str), new Strategy[0], new IStrategoTerm[0]));
		return context.getFactory().makeString(name + hash.substring(0, 8));
	}
}
