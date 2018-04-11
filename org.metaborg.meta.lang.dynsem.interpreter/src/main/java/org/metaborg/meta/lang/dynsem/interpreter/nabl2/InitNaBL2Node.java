package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.interpreter.InterpreterTerms;
import mb.nabl2.stratego.ConstraintTerms;
import mb.nabl2.terms.ITerm;

public class InitNaBL2Node extends DynSemNode {

	public InitNaBL2Node(SourceSection source) {
		super(source);
	}

	public void execute(VirtualFrame frame) {
		IStrategoTerm solution = getSolution();
		DynamicObject nabl2 = ObjectFactories.createNaBL2((IStrategoAppl) solution);

		ScopeIdentifier scopeIdentOne = new ScopeIdentifier("xmpl/fac.tig", "s_body-2");

		DynamicObject scope = scopeByIdent(scopeIdentOne, nabl2);
		System.out.println(solution);
		System.out.println("HERE: " + scope);
		System.out.println(ScopeEntryLayoutImpl.INSTANCE.getDeclarations(scope));
		System.out.println(declarations(nabl2));
	}

	public static DynamicObject graph(DynamicObject nabl2) {
		return NaBL2LayoutImpl.INSTANCE.getScopeGraph(nabl2);
	}

	public static DynamicObject declarations(DynamicObject nabl2) {
		return ScopeGraphLayoutImpl.INSTANCE.getDeclarations(graph(nabl2));
	}

	public static DynamicObject scopeByIdent(ScopeIdentifier ident, DynamicObject nabl2) {
		Object scope = ScopeGraphLayoutImpl.INSTANCE.getScopes(graph(nabl2))
				.get(ident);
		assert ScopeEntryLayoutImpl.INSTANCE.isScopeEntry(scope);
		return (DynamicObject) scope;
	}

	private NaBL2Context nabl2Context() {
		return (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
	}

	@TruffleBoundary
	protected IStrategoTerm getSolution() {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		return safeToStratego(InterpreterTerms.context(nabl2Context().getSolution()));
	}

	@TruffleBoundary
	private IStrategoTerm safeToStratego(ITerm term) {
		CompilerAsserts.neverPartOfCompilation("NaBL2 op should never be part of compilation");
		term = ConstraintTerms.explicate(term);
		return nabl2Context().getStrategoTerms().toStratego(term);
	}

}
