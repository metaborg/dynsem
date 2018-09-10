package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public final class Label extends ALabel {

	private String label;

	public Label(String label) {
		this.label = label;
	}

	@Override
	public int size() {
		return 1;
	}

	public String get_1() {
		return label;
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
		return label;
	}

	@Override
	@TruffleBoundary
	public int hashCode() {
		return 37 * label.hashCode() + 9534;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof Label && ((Label) obj).label.equals(this.label));
	}

	@NodeChildren({ @NodeChild(value = "label", type = TermBuild.class) })
	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		@Specialization
		public Label doBuild(String label) {
			return new Label(label);
		}

	}

	public abstract static class Match extends MatchPattern {

		@Child private MatchPattern label_pat;

		public Match(SourceSection source, MatchPattern label_pat) {
			super(source);
			this.label_pat = label_pat;
		}

		@Specialization
		public void doSpecific(VirtualFrame frame, Label other) {
			label_pat.executeMatch(frame, other.label);
		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object other) {
			if (other instanceof Label) {
				doSpecific(frame, (Label) other);
			} else {
				throw PremiseFailureException.SINGLETON;
			}
		}
	}

}
