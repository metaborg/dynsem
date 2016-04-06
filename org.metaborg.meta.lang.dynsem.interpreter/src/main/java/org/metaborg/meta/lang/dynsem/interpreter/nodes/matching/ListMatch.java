package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ListMatchFactory.ConsListMatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListMatch extends MatchPattern {

	public ListMatch(SourceSection source) {
		super(source);
	}

	public static final class NilListMatch extends ListMatch {

		public NilListMatch(SourceSection source) {
			super(source);
		}

		public static NilListMatch create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "List", 1);
			assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 0;
			return new NilListMatch(SourceSectionUtil.fromStrategoTerm(t));
		}

		private final ConditionProfile condProfile = ConditionProfile.createBinaryProfile();

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (condProfile.profile(BuiltinTypesGen.isIPersistentStack(term))) {
				return BuiltinTypesGen.asIPersistentStack(term).count() == 0;
			}
			return false;
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
			assert Tools.hasConstructor(t, "ListTail", 2);
			MatchPattern headPattern = MatchPattern.create(Tools.applAt(Tools.listAt(t, 0), 0), fd);
			MatchPattern tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);

			return ConsListMatchNodeGen.create(headPattern, tailPattern, SourceSectionUtil.fromStrategoTerm(t));
		}

		@Specialization
		public boolean execute(@SuppressWarnings("rawtypes") IPersistentStack term, VirtualFrame frame) {
			return stackCount(term) > 0 && headPattern.execute(stackPeek(term), frame)
					&& tailPattern.execute(stackPop(term), frame);
		}

		@TruffleBoundary
		private int stackCount(@SuppressWarnings("rawtypes") IPersistentStack s) {
			return s.count();
		}

		@TruffleBoundary
		private Object stackPeek(@SuppressWarnings("rawtypes") IPersistentStack s) {
			return s.peek();
		}

		@TruffleBoundary
		private Object stackPop(@SuppressWarnings("rawtypes") IPersistentStack s) {
			return s.pop();
		}

	}
}
