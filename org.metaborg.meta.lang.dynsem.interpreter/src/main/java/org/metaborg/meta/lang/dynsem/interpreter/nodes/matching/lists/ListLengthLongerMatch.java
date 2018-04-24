package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
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
	public boolean executeCachedList(VirtualFrame frame, IListTerm<?> list, @Cached("list") IListTerm<?> cachedL,
			@Cached("cachedL.size()") int cachedLength) {
		return minimalLength <= cachedLength;
	}

	@Specialization(replaces = "executeCachedList")
	public boolean executeList(VirtualFrame frame, IListTerm<?> list) {
		return minimalLength <= list.size();
	}

	public int getMinimalLength() {
		return minimalLength;
	}

}
