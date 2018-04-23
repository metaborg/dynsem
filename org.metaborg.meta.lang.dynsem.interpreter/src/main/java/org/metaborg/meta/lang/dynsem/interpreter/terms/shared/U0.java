package org.metaborg.meta.lang.dynsem.interpreter.terms.shared;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class U0 extends USort {

	public final static U0 SINGLETON = new U0();

	private U0() {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public ITermInstanceChecker getCheck() {
		return new ITermInstanceChecker() {
			@Override
			public boolean isInstance(Object obj) {
				return obj instanceof U0;
			}
		};
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
	public boolean equals(Object obj) {
		return (this == obj);
	}

	public static abstract class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		@Specialization
		public U0 execute() {
			return SINGLETON;
		}

	}

	public static abstract class Match extends MatchPattern {

		public Match(SourceSection source) {
			super(source);
		}

		@Specialization
		public boolean doDeepMatch(VirtualFrame frame, U0 term) {
			return true;
		}

		@Fallback
		public boolean doShallowFail(VirtualFrame frame, Object term) {
			return false;
		}

		public static Match create(SourceSection source) {
			return U0Factory.MatchNodeGen.create(source);
		}

	}

}
