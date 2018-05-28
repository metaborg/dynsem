package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.Optional;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.interpreter.InterpreterTerms;
import mb.nabl2.stratego.ConstraintTerms;
import mb.nabl2.stratego.StrategoTermIndices;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;

public abstract class DynSemNode extends Node {
	private final SourceSection sourceSection;

	public DynSemNode(SourceSection source) {
		super();
		this.sourceSection = source;
	}

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

	@CompilationFinal DynSemContext ctx;

	protected final DynSemContext getContext() {
		if (ctx == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			ctx = DynSemLanguage.getContext(getRootNode());
		}
		return ctx;
	}

	@CompilationFinal private NaBL2Context context;

	protected final NaBL2Context nabl2Context() {
		if (context == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			context = (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
		}
		return context;
	}

	protected IStrategoTerm getSolution() {
		return safeToStratego(InterpreterTerms.context(nabl2Context().getSolution()));
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
		return safeToStratego(val.get());
	}

	protected TermIndex getTermIndex(IStrategoTerm sterm) {
		if (sterm == null) {
			throw new IllegalArgumentException("Primitive must be called on an AST node.");
		}
		return StrategoTermIndices.get(sterm).orElseThrow(() -> new IllegalArgumentException("Node has no index."));
	}

	private IStrategoTerm safeToStratego(ITerm term) {
		term = ConstraintTerms.explicate(term);
		return nabl2Context().getStrategoTerms().toStratego(term);
	}

}
