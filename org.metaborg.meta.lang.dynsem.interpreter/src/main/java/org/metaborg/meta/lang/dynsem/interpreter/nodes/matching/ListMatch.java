package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListMatch extends MatchPattern {

	@Children protected final MatchPattern[] elemPatterns;
	@Child protected MatchPattern tailPattern;

	public ListMatch(SourceSection source, MatchPattern[] elemPatterns, MatchPattern tailPattern) {
		super(source);
		this.elemPatterns = elemPatterns;
		this.tailPattern = tailPattern;
	}

	@Specialization(guards = "tailPattern == null")
	public boolean doNoTail(VirtualFrame frame, ListTerm term) {
		if (term.size() != elemPatterns.length) {
			return false;
		}
		ListTerm tip = term;
		for (int i = 0; i < elemPatterns.length; i++) {
			if (!elemPatterns[i].executeMatch(frame, tip.head())) {
				return false;
			}
			tip = tip.tail();
		}
		return true;
	}

	@Specialization(guards = "tailPattern != null")
	public boolean doWithTail(VirtualFrame frame, ListTerm term) {
		if (term.size() < elemPatterns.length) {
			return false;
		}
		ListTerm tip = term;
		for (int i = 0; i < elemPatterns.length; i++) {
			if (!elemPatterns[i].executeMatch(frame, tip.head())) {
				return false;
			}
			tip = tip.tail();
		}
		return tailPattern.executeMatch(frame, tip);
	}

	public static ListMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3);

		IStrategoList elemTs = Tools.listAt(t, 0);
		final MatchPattern[] elemPatterns = new MatchPattern[elemTs.size()];
		for (int i = 0; i < elemPatterns.length; i++) {
			elemPatterns[i] = MatchPattern.create(Tools.applAt(elemTs, i), fd);
		}

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		}

		return ListMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), elemPatterns, tailPattern);
	}

}
