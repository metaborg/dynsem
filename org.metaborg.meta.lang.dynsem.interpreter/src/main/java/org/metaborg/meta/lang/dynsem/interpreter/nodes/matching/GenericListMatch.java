package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class GenericListMatch extends MatchPattern {

	protected final int numHeadElems;
	@Child protected MatchPattern tailPattern;

	public GenericListMatch(SourceSection source, int numHeadElems, MatchPattern tailPattern) {
		super(source);
		this.numHeadElems = numHeadElems;
		this.tailPattern = tailPattern;
	}

	@Specialization(guards = { "tailPattern == null", "numHeadElems != other.size()" })
	public void matchNoTailEqual(VirtualFrame frame, IListTerm<?> other) {
		;
	}

	@Specialization(guards = "tailPattern == null")
	public void matchNoTailNotEqual(VirtualFrame frame, IListTerm<?> other) {
		throw PatternMatchFailure.INSTANCE;
	}

	@Specialization(guards = "numHeadElems > other.size()")
	public void matchTailNotEqual(VirtualFrame frame, IListTerm<?> other) {
		throw PatternMatchFailure.INSTANCE;
	}

	@Specialization
	@ExplodeLoop
	public void matchTail(VirtualFrame frame, IListTerm<?> other) {
		IListTerm<?> tail = other;
		for (int i = 0; i < numHeadElems; i++) {
			tail = other.tail();
		}
		tailPattern.executeMatch(frame, tail);
	}

	@Fallback
	public void doFail(VirtualFrame frame, Object t) {
		throw PatternMatchFailure.INSTANCE;
	}

	public static GenericListMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "List", 1) || Tools.hasConstructor(t, "ListTail", 2);

		final int numHeadElems = Tools.listAt(t, 0).size();

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		}

		return GenericListMatchNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), numHeadElems, tailPattern);
	}

}
