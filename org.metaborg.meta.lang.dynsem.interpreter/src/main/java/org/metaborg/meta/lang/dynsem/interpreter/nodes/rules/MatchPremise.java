package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class MatchPremise extends Premise {

	@Child protected TermBuild term;
	@Child protected MatchPattern pat;

	public MatchPremise(TermBuild term, MatchPattern pattern,
			SourceSection source) {
		super(source);
		this.term = term;
		this.pat = pattern;
	}

	@Override
	public void execute(VirtualFrame frame) {
		ITerm trm = term.execute(frame);
		boolean matchsuccess = pat.execute(trm, frame);

		if (!matchsuccess) {
			throw new PremiseFailure();
		}
	}

}
