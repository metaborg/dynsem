package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
/*
 * TODO: specialize. cases:
 * 1. left is not a native op and right is WLD --> remove this node
 */
public class MatchPremise extends Premise {

	@Child protected TermBuild term;
	@Child protected MatchPattern patt;

	public MatchPremise(TermBuild term, MatchPattern pattern, SourceSection source) {
		super(source);
		this.term = term;
		this.patt = pattern;
	}

	@Override
	public void execute(VirtualFrame frame) {
		// evaluate LHS
		final Object t = term.executeGeneric(frame);

		// evaluate match
		patt.executeMatch(frame, t);
	}

	public static MatchPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Match", 2);
		TermBuild lhs = TermBuild.create(Tools.applAt(t, 0), fd);
		MatchPattern rhs = MatchPattern.create(Tools.applAt(t, 1), fd);
		return new MatchPremise(lhs, rhs, SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}
}
