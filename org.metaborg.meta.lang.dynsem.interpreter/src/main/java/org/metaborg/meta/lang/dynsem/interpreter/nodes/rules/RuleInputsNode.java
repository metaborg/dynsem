package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RuleInputsNode extends DynSemNode {

	@Child protected MatchPattern inPattern;
	@Children protected final MatchPattern[] componentPatterns;

	public RuleInputsNode(SourceSection source, MatchPattern inPattern, MatchPattern[] componentPatterns) {
		super(source);
		this.inPattern = inPattern;
		this.componentPatterns = componentPatterns;
	}

	public abstract void execute(VirtualFrame frame);

	@Specialization(guards = "guardConstantTerm(frame, term_cached, constantTermAssumption)", assumptions = "constantTermAssumption", limit = "1")
	public void doConstantTerm(VirtualFrame frame, @Cached("getInputTerm(frame)") Object term_cached,
			@Cached("getConstantInputAssumption()") Assumption constantTermAssumption) {
		doWithArguments(frame, term_cached, frame.getArguments());
	}

	@Specialization(replaces = "doConstantTerm")
	public void doUnstableTerm(VirtualFrame frame) {
		doWithArguments(frame, getInputTerm(frame), frame.getArguments());
	}

	@ExplodeLoop
	protected final void doWithArguments(VirtualFrame frame, Object inputTerm, Object[] args) {
		// evaluate the source pattern
		inPattern.executeMatch(frame, inputTerm);

		// evaluate the component patterns
		CompilerAsserts.compilationConstant(componentPatterns.length);
		for (int i = 0; i < componentPatterns.length; i++) {
			componentPatterns[i].executeMatch(frame, InterpreterUtils.getComponent(getContext(), args, i + 1, this));
		}
	}

	protected final Object getInputTerm(VirtualFrame frame) {
		return frame.getArguments()[0];
	}

	protected final boolean guardConstantTerm(VirtualFrame frame, Object term_cached,
			Assumption constantTermAssumption) {
		if (getInputTerm(frame) != term_cached) {
			constantTermAssumption.invalidate();
			return false;
		}
		return true;
	}

	public static RuleInputsNode create(IStrategoAppl lhsT, IStrategoList componentsT, FrameDescriptor fd) {
		MatchPattern[] lhsSemCompPatterns = new MatchPattern[componentsT.size()];
		for (int i = 0; i < lhsSemCompPatterns.length; i++) {
			lhsSemCompPatterns[i] = MatchPattern.create(Tools.applAt(componentsT, i), fd);
		}
		return RuleInputsNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(lhsT), MatchPattern.create(lhsT, fd),
				lhsSemCompPatterns);
	}
}
