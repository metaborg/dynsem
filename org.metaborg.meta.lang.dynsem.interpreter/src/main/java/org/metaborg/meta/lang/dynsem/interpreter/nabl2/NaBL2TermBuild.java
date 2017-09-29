package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import java.util.Optional;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.nabl2.interpreter.InterpreterTerms;
import org.metaborg.meta.nabl2.stratego.StrategoTermIndices;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.generic.TB;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public abstract class NaBL2TermBuild extends TermBuild {

	private final NaBL2Context context;

	public NaBL2TermBuild(SourceSection source) {
		super(source);
		context = (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
		if (context == null) {
			throw new IllegalStateException("No NaBL2 context available. "
					+ "Does the language use NaBL2, and was the interpreter invoked using the correct runner?");
		}
	}

	protected IStrategoTerm getSolution() {
		return context.getStrategoTerms().toStratego(InterpreterTerms.context(context.getSolution()));
	}

	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, String key) {
		TermIndex index = getTermIndex(sterm);
		ITerm keyterm = TB.newAppl(key);
		Optional<ITerm> val = context.getSolution().getAstProperties().getValue(index, keyterm);
		if (!val.isPresent()) {
			throw new IllegalArgumentException("Node has no type.");
		}
		return context.getStrategoTerms().toStratego(val.get());
	}

	protected TermIndex getTermIndex(IStrategoTerm sterm) {
		if (sterm == null) {
			throw new IllegalArgumentException("Primitive must be called on an AST node.");
		}
		return StrategoTermIndices.get(sterm).orElseThrow(() -> new IllegalArgumentException("Node has no index."));
	}

}