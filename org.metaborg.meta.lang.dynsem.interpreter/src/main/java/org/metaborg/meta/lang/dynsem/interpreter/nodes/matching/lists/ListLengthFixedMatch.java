package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListLengthFixedMatch extends MatchPattern {

	private final int expectedLength;

	public ListLengthFixedMatch(SourceSection source, int expectedLength) {
		super(source);
		this.expectedLength = expectedLength;
	}

	@Specialization(limit = "1", guards = { "list == cachedL" })
	public void executeCachedList(VirtualFrame frame, IListTerm<?> list, @Cached("list") IListTerm<?> cachedL,
			@Cached("cachedL.size()") int cachedLength) {
		if (cachedLength != expectedLength) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

	@Specialization(replaces = "executeCachedList")
	public void executeList(VirtualFrame frame, IListTerm<?> list) {
		if (list.size() != expectedLength) {
			throw PatternMatchFailure.INSTANCE;
		}
	}

	public int getExpectedLength() {
		return expectedLength;
	}

}
