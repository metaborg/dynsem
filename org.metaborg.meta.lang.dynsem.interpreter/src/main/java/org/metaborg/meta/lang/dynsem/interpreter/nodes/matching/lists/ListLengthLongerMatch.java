package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListLengthLongerMatch extends MatchPattern {

	private final int minimalLength;

	public ListLengthLongerMatch(SourceSection source, int minimalLength) {
		super(source);
		this.minimalLength = minimalLength;
	}

	@Specialization(limit = "1", guards = { "list == cachedL" })
	public void executeCachedList(VirtualFrame frame, IListTerm<?> list, @Cached("list") IListTerm<?> cachedL,
			@Cached("cachedL.size()") int cachedLength) {
		if (cachedLength <= minimalLength) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

	@Specialization(replaces = "executeCachedList")
	public void executeList(VirtualFrame frame, IListTerm<?> list) {
		if (list.size() <= minimalLength) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

	public int getMinimalLength() {
		return minimalLength;
	}

	// private final ConditionProfile onListProfile = ConditionProfile.createCountingProfile();
	//
	// @Fallback
	// public void executeFail(VirtualFrame frame, Object t) {
	// if (onListProfile.profile(t instanceof IListTerm<?>)) {
	// executeList(frame, (IListTerm<?>) t);
	// } else {
	// throw PatternMatchFailure.INSTANCE;
	// }
	// }

}
