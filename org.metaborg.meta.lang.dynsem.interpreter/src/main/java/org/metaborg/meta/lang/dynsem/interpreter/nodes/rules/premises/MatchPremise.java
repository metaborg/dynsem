package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.SortFunCallBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.NodeUtil.NodeCountFilter;
import com.oracle.truffle.api.source.SourceSection;

public class MatchPremise extends Premise {

	@Child protected TermBuild term;
	@Child protected MatchPattern patt;

	public MatchPremise(TermBuild term, MatchPattern pattern, SourceSection source) {
		super(source);
		this.term = term;
		this.patt = pattern;
	}

	private static final NodeCountFilter is_non_elidable_termbuild = new NodeCountFilter() {

		@Override
		public boolean isCounted(Node node) {
			return node instanceof SortFunCallBuild || node instanceof NativeOpBuild;
		}
	};

	@Override
	public void execute(VirtualFrame frame) {

		final Object t = term.executeGeneric(frame);
		patt.executeMatch(frame, t);
		// if (patt instanceof NoOpPattern && NodeUtil.countNodes(term, is_non_elidable_termbuild) == 0) {
		// // CompilerDirectives.transferToInterpreterAndInvalidate();
		// replace(NoOpPremiseNodeGen.create(getSourceSection()));
		// } else {
		// // CompilerDirectives.transferToInterpreterAndInvalidate();
		// replace(MatchPremiseFactory.NonElidableMatchPremiseNodeGen.create(getSourceSection(), patt, term))
		// .executeEvaluated(frame, t);
		// }
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

	@NodeChildren({ @NodeChild(value = "trm", type = TermBuild.class) })
	public abstract static class NonElidableMatchPremise extends Premise {

		@Child private MatchPattern patt;

		public NonElidableMatchPremise(SourceSection source, MatchPattern patt) {
			super(source);
			this.patt = patt;
		}

		public abstract void executeEvaluated(VirtualFrame f, Object trm);

		@Specialization
		public void executeWithEvaluatedChildren(VirtualFrame f, Object t) {
			patt.executeMatch(f, t);
		}

	}

}
