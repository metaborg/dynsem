package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public class CaseMatchPremise extends Premise {

	@Child private TermBuild termNode;
	@Children private final Case[] cases;

	public CaseMatchPremise(SourceSection source, TermBuild termNode, Case[] cases) {
		super(source);
		this.termNode = termNode;
		this.cases = cases;
	}

	@Override
	@ExplodeLoop
	public void execute(VirtualFrame frame) {
		Object t = termNode.executeGeneric(frame);

		boolean success = false;
		for (int i = 0; i < cases.length; i++) {
			success = cases[i].execute(frame, t);
			if (success) {
				break;
			}
		}
		if (!success) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

	public static CaseMatchPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "CaseMatch", 2);
		TermBuild tb = TermBuild.create(Tools.applAt(t, 0), fd);

		IStrategoList caseTs = Tools.listAt(t, 1);
		Case[] cases = new Case[caseTs.size()];

		for (int i = 0; i < cases.length; i++) {
			cases[i] = Case.create(Tools.applAt(caseTs, i), fd);
		}

		return new CaseMatchPremise(SourceSectionUtil.fromStrategoTerm(t), tb, cases);
	}

}
