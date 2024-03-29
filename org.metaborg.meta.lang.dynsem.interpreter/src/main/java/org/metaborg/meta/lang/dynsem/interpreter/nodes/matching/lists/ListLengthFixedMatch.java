package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListLengthFixedMatch extends MatchPattern {

	protected final int expectedLength;

	public ListLengthFixedMatch(SourceSection source, int expectedLength) {
		super(source);
		this.expectedLength = expectedLength;
	}

	@Specialization(guards = { "list == cachedL" })
	public void executeCachedList(IListTerm<?> list, @Cached("list") IListTerm<?> cachedL,
			@Cached("cachedL.size() == expectedLength") boolean eqLength) {
		if (!eqLength) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Specialization(replaces = "executeCachedList")
	public void executeList(IListTerm<?> list) {
		if (list.size() != expectedLength) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	public final int getExpectedLength() {
		return expectedLength;
	}

}
