package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListMatchPattern extends MatchPattern {

	public ListMatchPattern(SourceSection source) {
		super(source);
	}

	public static MatchPattern create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3)) {
			return createFromTypedList(t, fd);
		}

		if (Tools.hasConstructor(t, "List_", 1) || Tools.hasConstructor(t, "ListTail", 2)) {
			return createFromDeadHead(t, fd);
		}

		throw new IllegalStateException("Unknown list pattern: " + t);
	}

	private static MatchPattern createFromTypedList(IStrategoAppl t, FrameDescriptor fd) {
		IStrategoList elemTs = Tools.listAt(t, 0);
		MatchPattern pattern = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			pattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		} else {
			pattern = NilMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
		}

		for (int i = 0; i < elemTs.size(); i++) {
			pattern = ConsMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
					MatchPattern.create(Tools.applAt(elemTs, i), fd), pattern);
		}

		return pattern;
	}

	public static MatchPattern createFromDeadHead(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "List_", 1) || Tools.hasConstructor(t, "ListTail", 2);
		SourceSection source = SourceUtils.dynsemSourceSectionFromATerm(t);

		MatchPattern pattern = null;
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			pattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		} else {
			pattern = NilMatchNodeGen.create(source);
		}

		final int numHeadElems = Tools.listAt(t, 0).size();
		for (int i = 0; i < numHeadElems; i++) {
			pattern = ConsMatchNodeGen.create(source, NoOpPatternNodeGen.create(source), pattern);
		}

		return pattern;
	}

}
