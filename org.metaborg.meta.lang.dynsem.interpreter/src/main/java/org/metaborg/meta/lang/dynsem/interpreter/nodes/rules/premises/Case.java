package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Case extends DynSemNode {

	public Case(SourceSection source) {
		super(source);
	}

	public abstract boolean execute(VirtualFrame frame, Object t);

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

	public static Case create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "CaseOtherwise", 1)) {
			return CaseOtherwise.create(t, fd);
		}
		if (Tools.hasConstructor(t, "CasePattern", 2)) {
			return CasePattern.create(t, fd);
		}
		throw new NotImplementedException("Unsupported case: " + t);
	}

	public static class CaseOtherwise extends Case {

		@Children private final Premise[] premises;

		public CaseOtherwise(SourceSection source, Premise[] premises) {
			super(source);
			this.premises = premises;
		}

		@ExplodeLoop
		@Override
		public boolean execute(VirtualFrame frame, Object t) {
			for (int i = 0; i < premises.length; i++) {
				premises[i].execute(frame);
			}
			return true;
		}

		public static CaseOtherwise create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "CaseOtherwise", 1);
			IStrategoList premTs = Tools.listAt(t, 0);
			Premise[] premises = new Premise[premTs.size()];
			for (int i = 0; i < premises.length; i++) {
				premises[i] = Premise.create(Tools.applAt(premTs, i), fd);
			}
			return new CaseOtherwise(SourceSectionUtil.fromStrategoTerm(t), premises);
		}

	}

	public static class CasePattern extends Case {

		@Child private MatchPattern pattern;
		@Children private final Premise[] premises;

		public CasePattern(SourceSection source, MatchPattern pattern, Premise[] premises) {
			super(source);
			this.pattern = pattern;
			this.premises = premises;
		}

		@ExplodeLoop
		@Override
		public boolean execute(VirtualFrame frame, Object t) {
			if (pattern.execute(t, frame)) {
				for (int i = 0; i < premises.length; i++) {
					premises[i].execute(frame);
				}
				return true;
			} else {
				return false;
			}
		}

		public static CasePattern create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "CasePattern", 2);

			MatchPattern pattern = MatchPattern.create(Tools.applAt(t, 0), fd);

			IStrategoList premTs = Tools.listAt(t, 1);
			Premise[] premises = new Premise[premTs.size()];
			for (int i = 0; i < premises.length; i++) {
				premises[i] = Premise.create(Tools.applAt(premTs, i), fd);
			}
			return new CasePattern(SourceSectionUtil.fromStrategoTerm(t), pattern, premises);
		}

	}
}
