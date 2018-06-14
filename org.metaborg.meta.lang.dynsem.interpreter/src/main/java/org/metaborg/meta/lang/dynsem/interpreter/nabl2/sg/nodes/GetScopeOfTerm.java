package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import java.util.Objects;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2SolutionUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.constraints.ast.AstProperties;

@NodeChildren({ @NodeChild(value = "t", type = TermBuild.class) })
public abstract class GetScopeOfTerm extends NativeOpBuild {

	public GetScopeOfTerm(SourceSection source) {
		super(source);
	}

	public abstract ScopeIdentifier execute(VirtualFrame frame);

	@Specialization(limit = "1000", guards = { "t == t_cached" })
	public ScopeIdentifier executeCached(ITerm t, @Cached("t") ITerm t_cached,
			@Cached("getScopeIdentifier(t_cached)") ScopeIdentifier sid_cached) {
		return sid_cached;
	}

	@Specialization(replaces = "executeCached")
	public ScopeIdentifier executeUncached(ITerm t) {
		return getScopeIdentifier(t);
	}

	@TruffleBoundary
	protected ScopeIdentifier getScopeIdentifier(ITerm t) {
		IStrategoAppl scopeIdentT = (IStrategoAppl) NaBL2SolutionUtils.getAstProperty(Objects.requireNonNull(
				nabl2Context(),
				"No NaBL2 context available. Does the language use NaBL2, and was the interpreter invoked using the correct runner?"),
				t.getStrategoTerm(), AstProperties.key("bodyScope"));
		return ScopeIdentifier.create(scopeIdentT);
	}

	public static GetScopeOfTerm create(SourceSection source, TermBuild t) {
		return ScopeNodeFactories.createScopeOfTerm(source, t);
	}
}
