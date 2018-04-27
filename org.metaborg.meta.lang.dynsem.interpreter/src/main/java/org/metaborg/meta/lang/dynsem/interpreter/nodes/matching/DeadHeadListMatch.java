package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DeadHeadListMatch extends MatchPattern {

	protected final int numHeadElems;
	@Child protected MatchPattern tailPattern;

	public DeadHeadListMatch(SourceSection source, int numHeadElems, MatchPattern tailPattern) {
		super(source);
		this.numHeadElems = numHeadElems;
		this.tailPattern = tailPattern;
	}

	@Specialization(guards = "tailPattern == null")
	public boolean doNoTail(VirtualFrame frame, ListTerm list) {
		return numHeadElems == list.size();
	}

	@Specialization(guards = "tailPattern != null")
	public boolean doWithTail(VirtualFrame frame, ListTerm list) {
		return list.size() >= numHeadElems && tailPattern.executeMatch(frame, list.drop(numHeadElems));
	}

	public static DeadHeadListMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "List_", 1) || Tools.hasConstructor(t, "ListTail", 2);

		final int numHeadElems = Tools.listAt(t, 0).size();

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		}

		return DeadHeadListMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), numHeadElems, tailPattern);
	}

}
