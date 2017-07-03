package dynsem.metainterpreter.natives;

import org.metaborg.dynsem.metainterpreter.generated.terms.ApplT_2_Term;
import org.metaborg.dynsem.metainterpreter.generated.terms.ITTerm;
import org.metaborg.dynsem.metainterpreter.generated.terms.IntT_1_Term;
import org.metaborg.dynsem.metainterpreter.generated.terms.ListT_1_Term;
import org.metaborg.dynsem.metainterpreter.generated.terms.List_ITTerm;
import org.metaborg.dynsem.metainterpreter.generated.terms.StrT_1_Term;
import org.metaborg.dynsem.metainterpreter.generated.terms.TuplT_1_Term;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class TermConverter {
	public static ITTerm convert(IStrategoTerm t) {
		if (t instanceof IStrategoAppl)
			return convert((IStrategoAppl) t);

		if (t instanceof IStrategoTuple)
			return convert((IStrategoTuple) t);

		if (t instanceof IStrategoList)
			return convert((IStrategoList) t);

		if (t instanceof IStrategoString)
			return convert((IStrategoString) t);

		if (t instanceof IStrategoInt)
			return convert((IStrategoInt) t);

		throw new UnsupportedOperationException("Unsupported aterm " + t);
	}

	public static ApplT_2_Term convert(IStrategoAppl t) {
		return new ApplT_2_Term(t.getConstructor().getName(), convert(t.getAllSubterms()));
	}

	public static TuplT_1_Term convert(IStrategoTuple t) {
		return new TuplT_1_Term(convert(t.getAllSubterms()));
	}

	public static ListT_1_Term convert(IStrategoList ts) {
		return new ListT_1_Term(convert(ts.getAllSubterms()));
	}

	public static IntT_1_Term convert(IStrategoInt t) {
		return new IntT_1_Term(t.intValue());
	}

	public static StrT_1_Term convert(IStrategoString t) {
		return new StrT_1_Term(t.stringValue());
	}

	private static List_ITTerm convert(IStrategoTerm[] ats) {
		ITTerm[] ts = new ITTerm[ats.length];
		for (int i = 0; i < ats.length; i++) {
			ts[i] = convert(ats[i]);
		}
		return new List_ITTerm(ts);
	}

}
