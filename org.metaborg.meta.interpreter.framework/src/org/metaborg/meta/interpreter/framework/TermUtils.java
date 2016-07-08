package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class TermUtils {

	public static boolean boolFromTerm(IStrategoTerm term) {
		if (Tools.isTermAppl(term)) {
			IStrategoAppl tAppl = (IStrategoAppl) term;
			if (Tools.hasConstructor(tAppl, "___DS_False___", 0)) {
				return false;
			} else if (Tools.hasConstructor(tAppl, "___DS_True___", 0)) {
				return true;
			}
		}
		throw new MalformedASTException("Malformed boolean: " + term);
	}

	public static IStrategoTerm termFromBool(boolean bV, ITermFactory factory) {
		return factory.makeAppl(factory.makeConstructor(bV ? "___DS_True___"
				: "___DS_False___", 0));
	}

	public static int intFromTerm(IStrategoTerm term) {
		if (Tools.isTermInt(term)) {
			return Tools.asJavaInt(term);
		}
		throw new MalformedASTException("Malformed int: " + term);
	}

	public static IStrategoInt termFromInt(int i, ITermFactory factory) {
		return factory.makeInt(i);
	}

	public static long longFromTerm(IStrategoTerm subterm) {
		throw new RuntimeException("Unsupported number format");
	}

	public static double doubleFromTerm(IStrategoTerm term) {
		if (Tools.isTermReal(term)) {
			return Tools.asJavaDouble(term);
		}
		throw new MalformedASTException("Malformed double: " + term);
	}

	public static IStrategoReal termFromDouble(double d, ITermFactory factory) {
		return factory.makeReal(d);
	}

	public static String stringFromTerm(IStrategoTerm term) {
		if (Tools.isTermString(term)) {
			return Tools.asJavaString(term);
		}
		throw new MalformedASTException("Malformed string: " + term);
	}

	public static IStrategoString termFromString(String s, ITermFactory factory) {
		return factory.makeString(s);
	}

	public static IStrategoTerm termFromLong(long _1, ITermFactory factory) {
		throw new RuntimeException("Unsupported number format");
	}

}
