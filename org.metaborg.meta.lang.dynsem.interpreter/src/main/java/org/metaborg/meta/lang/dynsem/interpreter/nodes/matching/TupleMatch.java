package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.TupleTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TupleMatch extends MatchPattern {

	@Children private final MatchPattern[] elemPatterns;

	public TupleMatch(SourceSection source, MatchPattern[] elemPatterns) {
		super(source);
		this.elemPatterns = elemPatterns;
	}

	@Specialization
	@ExplodeLoop
	public boolean doTuple(VirtualFrame frame, TupleTerm tupl) {
		if (elemPatterns.length != tupl.size()) {
			return false;
		}

		Object[] subterms = tupl.subterms();
		for (int i = 0; i < elemPatterns.length; i++) {
			if (!elemPatterns[i].executeMatch(frame, subterms[i])) {
				return false;
			}
		}
		return true;
	}

	public static TupleMatch create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedTuple", 2);

		IStrategoList childrenT = Tools.listAt(t, 0);
		MatchPattern[] children = new MatchPattern[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = MatchPattern.create(Tools.applAt(childrenT, i), fd);
		}

		return TupleMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), children);
	}

}
