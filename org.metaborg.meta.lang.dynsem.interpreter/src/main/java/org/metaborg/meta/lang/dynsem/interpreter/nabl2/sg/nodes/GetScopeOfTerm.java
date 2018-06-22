package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

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
		IStrategoTerm scopeIdentT = getAstProperty(t.getStrategoTerm(), "bodyScope");
		return ScopeIdentifier.create(scopeIdentT);
	}

	public static GetScopeOfTerm create(SourceSection source, TermBuild t) {
		return ScopeNodeFactories.createScopeOfTerm(source, t);
	}
}
