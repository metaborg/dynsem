package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2SolutionUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
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
public abstract class GetScopeIdentifierOfITermNode extends DynSemNode {

	public GetScopeIdentifierOfITermNode(SourceSection source) {
		super(source);
	}

	public abstract ScopeIdentifier execute(VirtualFrame frame);

	@Specialization(guards = { "t == t_cached" })
	public ScopeIdentifier executeCached(ITerm t, @Cached("t") ITerm t_cached,
			@Cached("getScopeIdentifier(t_cached)") ScopeIdentifier sid_cached) {
		return sid_cached;
	}

	@Specialization(replaces = "executeCached")
	public ScopeIdentifier executeUncached(ITerm t) {
		return getScopeIdentifier(t);
	}

	@TruffleBoundary
	private ScopeIdentifier getScopeIdentifier(ITerm t) {
		IStrategoAppl scopeIdentT = (IStrategoAppl) NaBL2SolutionUtils.getAstProperty(nabl2Context(),
				t.getStrategoTerm(), AstProperties.key("bodyScope"));
		return ScopeIdentifier.create(scopeIdentT);
	}
}
