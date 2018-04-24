package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
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
	public boolean doNoTail(VirtualFrame frame, IListTerm<?> list) {
		return numHeadElems == list.size();
	}

	@Specialization(guards = "tailPattern != null")
	public boolean doWithTail(VirtualFrame frame, IListTerm<?> list) {
		return list.size() >= numHeadElems && tailPattern.executeMatch(frame, list.drop(numHeadElems));
	}

	public static GenericListMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "List_", 1) || Tools.hasConstructor(t, "ListTail", 2);

		final int numHeadElems = Tools.listAt(t, 0).size();

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		}

		return GenericListMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), numHeadElems, tailPattern);
	}

}
