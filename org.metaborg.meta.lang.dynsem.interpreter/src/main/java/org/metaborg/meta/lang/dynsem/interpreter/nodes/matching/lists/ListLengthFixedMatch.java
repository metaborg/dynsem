package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListLengthFixedMatch extends MatchPattern {

	protected final int expectedLength;

	public ListLengthFixedMatch(SourceSection source, int expectedLength) {
		super(source);
		this.expectedLength = expectedLength;
	}

	private final ConditionProfile profile = ConditionProfile.createCountingProfile();

	@Specialization(guards = { "list == cachedL" })
	public boolean executeCachedList(VirtualFrame frame, IListTerm<?> list, @Cached("list") IListTerm<?> cachedL,
			@Cached("cachedL.size() == expectedLength") boolean eqLength) {
		return eqLength;
	}

	@Specialization(replaces = "executeCachedList")
	public boolean executeList(VirtualFrame frame, IListTerm<?> list) {
		return profile.profile(list.size() == expectedLength);
	}

}
