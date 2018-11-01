package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import java.util.Optional;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.constraints.ast.AstProperties;
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

	protected final Assumption getConstantInputAssumption() {
		return ((DynSemRootNode) getRootNode()).getConstantTermAssumption();
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
		NaBL2Context nabl2ctx = nabl2Context();
		return safeToStratego(nabl2ctx, InterpreterTerms.context(nabl2ctx.getSolution()));
	}

	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, String key) {
		return getAstProperty(sterm, getAstPropertyKey(key));
	}

	protected IStrategoTerm getAstProperty(IStrategoTerm sterm, ITerm key) {
		TermIndex index = getTermIndex(sterm);
		NaBL2Context nabl2ctx = nabl2Context();
		Optional<ITerm> val = internal_getPropertyValue(nabl2ctx, index, key);
		if (!val.isPresent()) {
			throw new IllegalArgumentException("Node has no " + key + " parameter");
		}
		return safeToStratego(nabl2ctx, val.get());
	}

	@TruffleBoundary
	private static Optional<ITerm> internal_getPropertyValue(NaBL2Context nabl2ctx, TermIndex index, ITerm key) {
		Optional<ITerm> prop = nabl2ctx.getSolution().astProperties().getValue(index, key);
		return prop;
	}

	@TruffleBoundary
	protected static TermIndex getTermIndex(IStrategoTerm sterm) {
		if (sterm == null) {
			throw new IllegalArgumentException("Primitive must be called on an AST node.");
		}
		return StrategoTermIndices.get(sterm).orElseThrow(() -> new IllegalArgumentException("Node has no index."));
	}

	@TruffleBoundary
	private static IStrategoTerm safeToStratego(NaBL2Context nabl2ctx, ITerm term) {
		term = ConstraintTerms.explicate(term);
		return nabl2ctx.getStrategoTerms().toStratego(term);
	}

	@TruffleBoundary
	protected static ITerm getAstPropertyKey(String propName) {
		return AstProperties.key(propName);
	}

}
