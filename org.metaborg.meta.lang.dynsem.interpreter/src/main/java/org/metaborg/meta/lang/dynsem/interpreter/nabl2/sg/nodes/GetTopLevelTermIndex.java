package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2SolutionUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.TermIndex;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "term", type = TermBuild.class)
public abstract class GetTopLevelTermIndex extends NativeOpBuild {

	public GetTopLevelTermIndex(SourceSection source) {
		super(source);
	}

	@Specialization(guards = "term_cached == term", limit = "100")
	public TermIndex doCached(ITerm term, @Cached("term") ITerm term_cached,
			@Cached("doUncached(term_cached)") TermIndex index_cached) {
		return index_cached;
	}

	@Specialization
	public TermIndex doUncached(ITerm term) {
		mb.nabl2.terms.stratego.TermIndex termIndex = NaBL2SolutionUtils.getTermIndex(term.getStrategoTerm());

		return new TermIndex(termIndex.getResource(), 0);
	}

	public static GetTopLevelTermIndex create(SourceSection source, TermBuild term) {
		return ScopeNodeFactories.createGetTopLevelTermIndex(source, term);
	}
}
