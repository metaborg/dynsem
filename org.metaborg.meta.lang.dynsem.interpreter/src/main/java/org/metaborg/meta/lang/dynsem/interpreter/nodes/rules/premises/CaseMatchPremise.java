package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
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

}
