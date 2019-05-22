package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import mb.nabl2.interpreter.InterpreterTerms;
import mb.nabl2.stratego.ConstraintTerms;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.stratego.StrategoTermIndex;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.nabl2.terms.stratego.TermIndex;

public final class NaBL2SolutionUtils {

	private NaBL2SolutionUtils() {
	}

	@TruffleBoundary
	public static IStrategoTerm getSolution(NaBL2Context nabl2Context) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		return safeToStratego(nabl2Context, InterpreterTerms.context(nabl2Context.getSolution()));
	}

	@TruffleBoundary
	public static IStrategoTerm getAstProperty(NaBL2Context nabl2Context, IStrategoTerm sterm, String key) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		return getAstProperty(nabl2Context, sterm, B.newAppl(key));
	}

	@TruffleBoundary
	public static IStrategoTerm getAstProperty(NaBL2Context nabl2Context, IStrategoTerm sterm, ITerm key) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		TermIndex index = getTermIndex(sterm);
		Optional<ITerm> val = nabl2Context.getSolution().astProperties().getValue(index, key);
		if (!val.isPresent()) {
			throw new IllegalArgumentException("Node has no " + key + " parameter");
		}
		return safeToStratego(nabl2Context, val.get());
	}

	@TruffleBoundary
	public static TermIndex getTermIndex(IStrategoTerm sterm) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		final StrategoTerms strategoTerms = new StrategoTerms();
		return strategoTerms.fromStratego(getStrategoTermIndex(sterm));
	}

	@TruffleBoundary
	public static StrategoTermIndex getStrategoTermIndex(IStrategoTerm sterm) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		if (sterm == null) {
			throw new IllegalArgumentException("Primitive must be called on an AST node.");
		}
		return StrategoTermIndex.get(sterm).orElseThrow(() -> new IllegalArgumentException("Node has no index."));
	}

	@TruffleBoundary
	public static IStrategoTerm safeToStratego(NaBL2Context nabl2Context, ITerm term) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		return nabl2Context.getStrategoTerms().toStratego(ConstraintTerms.explicate(term));
	}
}
