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

public final class D extends ALabel {

	public final static D SINGLETON = new D();

	private D() {
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
		return "D()";
	}

	@Override
	public int hashCode() {
		return 95366934;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof D);
	}

	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		@Specialization
		public D doBuild() {
			return D.SINGLETON;
		}

	}

	public abstract static class Match extends MatchPattern {

		public Match(SourceSection source) {
			super(source);
		}

		@Specialization
		public void doSpecific(VirtualFrame frame, D other) {

		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object other) {
			if (!(other instanceof D)) {
				throw PremiseFailureException.SINGLETON;
			}
		}
	}

}
