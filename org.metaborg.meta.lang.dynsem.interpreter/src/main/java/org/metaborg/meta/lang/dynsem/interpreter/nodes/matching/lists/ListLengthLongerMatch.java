package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
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
		if (minimalLength > cachedLength) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Specialization(replaces = "executeCachedList")
	public void executeList(VirtualFrame frame, IListTerm<?> list) {
		if (minimalLength > list.size()) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	public int getMinimalLength() {
		return minimalLength;
	}

}
