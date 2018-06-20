package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public final class I extends ALabel {

	public final static I SINGLETON = new I();

	private I() {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public ITermInstanceChecker getCheck() {
		return null;
	}

	@Override
	public boolean hasStrategoTerm() {
		return false;
	}

	@Override
	public IStrategoTerm getStrategoTerm() {
		return null;
	}

	@Override
	public String toString() {
		return "I()";
	}

	@Override
	public int hashCode() {
		return 144865439;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof I);
	}

	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		@Specialization
		public I doBuild() {
			return I.SINGLETON;
		}

	}

	public abstract static class Match extends MatchPattern {

		public Match(SourceSection source) {
			super(source);
		}

		@Specialization
		public void doSpecific(VirtualFrame frame, I other) {

		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object other) {
			if (!(other instanceof I)) {
				throw PremiseFailureException.SINGLETON;
			}
		}
	}

}
