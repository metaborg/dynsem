package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class), @NodeChild(value = "right", type = TermBuild.class) })
public abstract class TermEqPremise extends Premise {

	public TermEqPremise(SourceSection source) {
		super(source);
	}

	public static TermEqPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "TermEq", 2);
		TermBuild lhs = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild rhs = TermBuild.create(Tools.applAt(t, 1), fd);
		return TermEqPremiseNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), lhs, rhs);
	}

	// TODO specialize for different types of left & right
	
	@Specialization
	public void doEvaluated(Object left, Object right) {
		if (!left.equals(right)) {
			throw new PremiseFailure();
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}
}
