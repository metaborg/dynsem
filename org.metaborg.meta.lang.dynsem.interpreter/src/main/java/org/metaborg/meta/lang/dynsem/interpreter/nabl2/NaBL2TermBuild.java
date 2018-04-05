package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.Optional;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.interpreter.InterpreterTerms;
import mb.nabl2.stratego.ConstraintTerms;
import mb.nabl2.stratego.StrategoTermIndices;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;

public abstract class NaBL2TermBuild extends NativeOpBuild {

	@CompilationFinal private NaBL2Context context;

	public NaBL2TermBuild(SourceSection source) {
		super(source);
	}

	@TruffleBoundary
	private NaBL2Context nabl2Context() {
		if (context == null) {
			CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
			context = (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
			if (context == null) {
				throw new IllegalStateException("No NaBL2 context available. "
						+ "Does the language use NaBL2, and was the interpreter invoked using the correct runner?");
			}
		}
		return context;
	}

	@TruffleBoundary
	protected IStrategoTerm getSolution() {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		return safeToStratego(InterpreterTerms.context(nabl2Context().getSolution()));
	}

	@TruffleBoundary
	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, String key) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		return getAstProperty(sterm, B.newAppl(key));
	}

	@TruffleBoundary
	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, ITerm key) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		TermIndex index = getTermIndex(sterm);
		Optional<ITerm> val = nabl2Context().getSolution().astProperties().getValue(index, key);
		if (!val.isPresent()) {
			throw new IllegalArgumentException("Node has no " + key + " parameter");
		}
		return safeToStratego(val.get());
	}

	@TruffleBoundary
	protected TermIndex getTermIndex(IStrategoTerm sterm) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		if (sterm == null) {
			throw new IllegalArgumentException("Primitive must be called on an AST node.");
		}
		return StrategoTermIndices.get(sterm).orElseThrow(() -> new IllegalArgumentException("Node has no index."));
	}

	@TruffleBoundary
	private IStrategoTerm safeToStratego(ITerm term) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		term = ConstraintTerms.explicate(term);
		return nabl2Context().getStrategoTerms().toStratego(term);
	}

}
