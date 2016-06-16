package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RuleInputsNode extends DynSemNode {

	@Child protected MatchPattern inPattern;
	@Children protected final MatchPattern[] componentPatterns;

	public RuleInputsNode(SourceSection source, MatchPattern inPattern, MatchPattern[] componentPatterns) {
		super(source);
		this.inPattern = inPattern;
		this.componentPatterns = componentPatterns;
	}

	public void execute(VirtualFrame frame) {
		final Object[] args = frame.getArguments();

		// evaluate the source pattern
		inPattern.executeMatch(frame, args[0]);

		// evaluate the component patterns
		evaluateComponentPatterns(frame, args);
	}

	@ExplodeLoop
	private void evaluateComponentPatterns(VirtualFrame frame, Object[] args) {
		CompilerAsserts.compilationConstant(componentPatterns.length);
		for (int i = 0; i < componentPatterns.length; i++) {
			componentPatterns[i].executeMatch(frame, InterpreterUtils.getComponent(args, i + 1));
		}
	}

	public static RuleInputsNode create(IStrategoAppl lhsT, IStrategoList componentsT, FrameDescriptor fd) {
		MatchPattern[] lhsSemCompPatterns = new MatchPattern[componentsT.size()];
		for (int i = 0; i < lhsSemCompPatterns.length; i++) {
			lhsSemCompPatterns[i] = MatchPattern.create(Tools.applAt(componentsT, i), fd);
		}
		return new RuleInputsNode(SourceSectionUtil.fromStrategoTerm(lhsT), MatchPattern.create(lhsT, fd),
				lhsSemCompPatterns);
	}
}
