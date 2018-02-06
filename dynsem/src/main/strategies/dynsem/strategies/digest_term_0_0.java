package dynsem.strategies;

import java.nio.charset.StandardCharsets;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import com.google.common.hash.Hashing;

public class digest_term_0_0 extends Strategy {
	public static digest_term_0_0 instance = new digest_term_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		final String str = current.toString();
		final String hash = Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
		return context.getFactory().makeString(hash);
	}
}
