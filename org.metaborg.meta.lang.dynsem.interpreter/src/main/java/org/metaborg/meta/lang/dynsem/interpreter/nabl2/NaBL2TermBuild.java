package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.Optional;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.interpreter.InterpreterTerms;
import mb.nabl2.stratego.StrategoTermIndices;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;

public abstract class NaBL2TermBuild extends TermBuild {

	@CompilationFinal private NaBL2Context context;

	public NaBL2TermBuild(SourceSection source) {
		super(source);
	}

	private NaBL2Context nabl2Context() {
		if (context == null) {
			context = (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
			if (context == null) {
				throw new IllegalStateException("No NaBL2 context available. "
						+ "Does the language use NaBL2, and was the interpreter invoked using the correct runner?");
			}
		}
		return context;
	}

	protected IStrategoTerm getSolution() {
		return nabl2Context().getStrategoTerms().toStratego(InterpreterTerms.context(context.getSolution()));
	}

	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, String key) {
		return getAstProperty(sterm, B.newAppl(key));
	}

	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, ITerm key) {
		TermIndex index = getTermIndex(sterm);
		Optional<ITerm> val = nabl2Context().getSolution().astProperties().getValue(index, key);
		if (!val.isPresent()) {
			throw new IllegalArgumentException("Node has no " + key + " parameter");
		}
		return nabl2Context().getStrategoTerms().toStratego(val.get());
	}

	protected TermIndex getTermIndex(IStrategoTerm sterm) {
		if (sterm == null) {
			throw new IllegalArgumentException("Primitive must be called on an AST node.");
		}
		return StrategoTermIndices.get(sterm).orElseThrow(() -> new IllegalArgumentException("Node has no index."));
	}

}
