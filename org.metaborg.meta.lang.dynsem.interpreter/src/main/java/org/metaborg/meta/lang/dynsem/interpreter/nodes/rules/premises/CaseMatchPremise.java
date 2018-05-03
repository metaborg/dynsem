package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "termNode", type = TermBuild.class) })
public abstract class CaseMatchPremise extends Premise {

	@Child private Case2 caseChain;

	public CaseMatchPremise(SourceSection source, Case2 caseChain) {
		super(source);
		this.caseChain = caseChain;
	}

	@Specialization
	public void executeCases(VirtualFrame frame, Object t) {
		caseChain.execute(frame, t);
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

	public static CaseMatchPremise create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "CaseMatch", 2);
		TermBuild tb = TermBuild.create(Tools.applAt(t, 0), fd);

		return CaseMatchPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				Case2.create(lang, Tools.listAt(t, 1), fd), tb);
	}

}
