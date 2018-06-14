package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2SolutionUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.TermIndex;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "term", type = TermBuild.class)
public abstract class GetTopLevelTermIndex extends NativeOpBuild {

	public GetTopLevelTermIndex(SourceSection source) {
		super(source);
	}

	@Specialization
	public TermIndex executeTermIndex(ITerm term) {
		mb.nabl2.stratego.TermIndex termIndex = NaBL2SolutionUtils.getTermIndex(nabl2Context(), term.getStrategoTerm());

		return new TermIndex(termIndex.getResource(), 0);
	}

	public static GetTopLevelTermIndex create(SourceSection source, TermBuild term) {
		return ScopeNodeFactories.createGetTopLevelTermIndex(source, term);
	}
}
