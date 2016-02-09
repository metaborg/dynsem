package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ListBuild.ConsListBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ListBuildFactory.ConsListBuildNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ListMatchFactory.ConsListMatchNodeGen;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
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
			assert Tools.isTermList(t.getSubterm(0))
					&& Tools.listAt(t, 0).size() == 0;
			return new NilListMatch(SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (BuiltinTypesGen.isIPersistentStack(term)) {
				return BuiltinTypesGen.asIPersistentStack(term).count() == 0;
			}
			return false;
		}
	}

	public static abstract class ConsListMatch extends ListMatch {

		@Child protected MatchPattern headPattern;
		@Child protected MatchPattern tailPattern;

		public ConsListMatch(MatchPattern headPattern,
				MatchPattern tailPattern, SourceSection source) {
			super(source);
			this.headPattern = headPattern;
			this.tailPattern = tailPattern;
		}

		public static ConsListMatch create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "ListTail", 2);
			MatchPattern headPattern = MatchPattern.create(Tools.applAt(t, 0),
					fd);
			MatchPattern tailPattern = MatchPattern.create(Tools.applAt(t, 1),
					fd);

			return ConsListMatchNodeGen.create(headPattern, tailPattern,
					SourceSectionUtil.fromStrategoTerm(t));
		}

		@SuppressWarnings("rawtypes")
		@Specialization
		public boolean execute(IPersistentStack term, VirtualFrame frame) {
			return term.count() > 0 && headPattern.execute(term.peek(), frame)
					&& tailPattern.execute(term.pop(), frame);
		}

	}
}
