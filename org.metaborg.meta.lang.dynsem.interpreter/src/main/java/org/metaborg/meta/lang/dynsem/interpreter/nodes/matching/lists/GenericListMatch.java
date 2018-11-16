package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class GenericListMatch extends MatchPattern {

	protected final int numHeadElems;
	@Child protected MatchPattern tailPattern;

	public GenericListMatch(SourceSection source, int numHeadElems, MatchPattern tailPattern) {
		super(source);
		this.numHeadElems = numHeadElems;
		this.tailPattern = tailPattern;
	}

	@Specialization(guards = "tailPattern == null")
	public void doNoTail(VirtualFrame frame, IListTerm<?> list) {
		if (numHeadElems != list.size()) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@SuppressWarnings("rawtypes")
	@Specialization(guards = { "tailPattern != null", "list == list_cached", "list.size() >= numHeadElems" })
	public void doWithTailCached(VirtualFrame frame, IListTerm list, @Cached("list") IListTerm list_cached,
			@Cached("list.drop(numHeadElems)") IListTerm tail) {
		tailPattern.executeMatch(frame, tail);
	}

	@Specialization(replaces = "doWithTailCached", guards = "tailPattern != null")
	public void doWithTail(VirtualFrame frame, IListTerm<?> list) {
		if (list.size() < numHeadElems) {
			throw PremiseFailureException.SINGLETON;
		}
		tailPattern.executeMatch(frame, list.drop(numHeadElems));
	}

}
