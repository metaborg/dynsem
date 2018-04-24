package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.ConditionProfile;
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

	@Specialization
	@ExplodeLoop
	public void executeWithProfiling(VirtualFrame frame, @Cached("createBinaryProfile()") ConditionProfile profile1,
			@Cached("createCountingProfile()") ConditionProfile profile2) {
		final Object[] args = frame.getArguments();

		// evaluate the source pattern
		if (!profile1.profile(inPattern.executeMatch(frame, args[0]))) {
			throw PremiseFailureException.SINGLETON;
		}

		// evaluate the component patterns
		CompilerAsserts.compilationConstant(componentPatterns.length);
		for (int i = 0; i < componentPatterns.length; i++) {
			if (!profile2.profile(componentPatterns[i].executeMatch(frame,
					InterpreterUtils.getComponent(getContext(), args, i + 1)))) {
				throw PremiseFailureException.SINGLETON;
			}
		}
	}


	public static RuleInputsNode create(IStrategoAppl lhsT, IStrategoList componentsT, FrameDescriptor fd) {
		MatchPattern[] lhsSemCompPatterns = new MatchPattern[componentsT.size()];
		for (int i = 0; i < lhsSemCompPatterns.length; i++) {
			lhsSemCompPatterns[i] = MatchPattern.create(Tools.applAt(componentsT, i), fd);
		}
		return RuleInputsNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(lhsT),
				MatchPattern.create(lhsT, fd),
				lhsSemCompPatterns);
	}
}
