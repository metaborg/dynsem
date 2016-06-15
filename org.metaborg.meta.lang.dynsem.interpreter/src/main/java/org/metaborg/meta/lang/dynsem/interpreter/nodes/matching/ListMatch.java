package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ListMatchFactory.ConsListMatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ListMatchFactory.NilListMatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListMatch extends MatchPattern {

	public ListMatch(SourceSection source) {
		super(source);
	}

	@TruffleBoundary
	protected int stackCount(@SuppressWarnings("rawtypes") IPersistentStack s) {
		return s.count();
	}

	@TruffleBoundary
	protected Object stackPeek(@SuppressWarnings("rawtypes") IPersistentStack s) {
		return s.peek();
	}

	@TruffleBoundary
	protected Object stackPop(@SuppressWarnings("rawtypes") IPersistentStack s) {
		return s.pop();
	}

	public static abstract class NilListMatch extends ListMatch {

		public NilListMatch(SourceSection source) {
			super(source);
		}

		public static NilListMatch create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "List", 1);
			assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 0;
			return NilListMatchNodeGen.create(SourceSectionUtil.fromStrategoTerm(t));
		}

		@Specialization(guards = "stackCount(t) == 0")
		public void doSuccess(VirtualFrame frame, IPersistentStack<?> t) {

		}

		@Specialization
		public void doFailure(VirtualFrame frame, IPersistentStack<?> t) {
			throw PatternMatchFailure.INSTANCE;
		}

	}

	public static abstract class ConsListMatch extends ListMatch {

		@Child protected MatchPattern headPattern;
		@Child protected MatchPattern tailPattern;

		public ConsListMatch(MatchPattern headPattern, MatchPattern tailPattern, SourceSection source) {
			super(source);
			this.headPattern = headPattern;
			this.tailPattern = tailPattern;
		}

		public static ConsListMatch create(IStrategoAppl t, FrameDescriptor fd) {
			CompilerAsserts.neverPartOfCompilation();
			assert Tools.hasConstructor(t, "ListTail", 2);
			MatchPattern headPattern = MatchPattern.create(Tools.applAt(Tools.listAt(t, 0), 0), fd);
			MatchPattern tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);

			return ConsListMatchNodeGen.create(headPattern, tailPattern, SourceSectionUtil.fromStrategoTerm(t));
		}

		@Specialization
		public void doList(VirtualFrame frame, IPersistentStack<?> list) {
			if (stackCount(list) > 0) {
				headPattern.executeMatch(frame, stackPeek(list));
				tailPattern.executeMatch(frame, stackPop(list));
			} else {
				throw PatternMatchFailure.INSTANCE;
			}
		}

	}
}
