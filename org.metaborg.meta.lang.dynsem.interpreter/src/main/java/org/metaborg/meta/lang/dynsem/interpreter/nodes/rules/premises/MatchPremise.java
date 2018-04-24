package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.SortFunCallBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.NoOpPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
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
		if (patt instanceof NoOpPattern && NodeUtil.countNodes(term, is_non_elidable_termbuild) == 0) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			replace(NoOpPremiseNodeGen.create(getSourceSection()));
		} else {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			replace(MatchPremiseFactory.NonElidableMatchPremiseNodeGen.create(getSourceSection(),
					NodeUtil.cloneNode(term), NodeUtil.cloneNode(patt))).executeEvaluated(frame, t);
		}
	}

	public static MatchPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Match", 2);
		TermBuild lhs = TermBuild.create(Tools.applAt(t, 0), fd);
		MatchPattern rhs = MatchPattern.create(Tools.applAt(t, 1), fd);
		return new MatchPremise(lhs, rhs, SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

	@NodeChildren({ @NodeChild(value = "trm", type = TermBuild.class),
			@NodeChild(value = "patt", type = MatchPattern.class, executeWith = "trm") })
	public abstract static class NonElidableMatchPremise extends Premise {

		public NonElidableMatchPremise(SourceSection source) {
			super(source);
		}

		public abstract void executeEvaluated(VirtualFrame f, Object trm);

		@Specialization
		public void executeWithEvaluatedChildren(Object t, boolean matched) {
			if (!matched) {
				throw PremiseFailureException.SINGLETON;
			}
		}

		@TruffleBoundary
		public void dumpTerm(Object t) {
			System.out.println(t);
		}

	}

}
