package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public class InlinedDispatch extends AbstractDispatch {

	@Child protected InlinedRuleChain dispatchChain;
	private final Class<?> dispatchClass;

	public InlinedDispatch(SourceSection source, Class<?> dispatchClass, String arrowName) {
		super(source, arrowName);
		this.dispatchClass = dispatchClass;
	}

	@Override
	public RuleResult execute(Object[] args) {
		return executeHelper(args, true);
	}

	private RuleResult executeHelper(Object[] args, boolean deepAllowed) {
		try {
			CompilerAsserts.compilationConstant(dispatchChain);
			if (dispatchChain == null) {
				growChain();
			}
			return dispatchChain.execute(args, deepAllowed);
		} catch (PremiseFailureException pmfex) {
			growChain();
			return executeHelper(args, false);
		}
	}

	public static InlinedDispatch create(Class<?> dispatchClass, String arrowName) {
		return new InlinedDispatch(SourceUtils.dynsemSourceSectionUnvailable(), dispatchClass, arrowName);
	}

	protected RuleNode createRuleForInlining(DynSemLanguage language, IStrategoAppl ruleSourceTerm,
			FrameDescriptor frameDescriptor, ITermRegistry termReg) {
		return RuleNode.create(language, ruleSourceTerm, frameDescriptor, termReg);
	}

	private void growChain() {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		IRuleRegistry ruleReg = getContext().getRuleRegistry();
		RuleRootNode[] roots = ruleReg.lookupRuleRoots(arrowName, dispatchClass);
		int currentChainLength = dispatchChain != null ? dispatchChain.length() : 0;

		if (currentChainLength >= roots.length) {
			// FIXME: of course, we should attempt a sort-wide rule
			throw new ReductionFailure("No more rules to try. And sort-dispatch is NOT IMPLEMENTED",
					InterpreterUtils.createStacktrace(), this);
		}

		RuleRootNode nextRoot = roots[currentChainLength];
		FrameDescriptor frameDescriptor = nextRoot.getFrameDescriptor();
		RuleNode inlineableRule = createRuleForInlining(ruleReg.getLanguage(), nextRoot.getRuleSourceTerm(),
				frameDescriptor, getContext().getTermRegistry());
		this.dispatchChain = insert(
				new InlinedRuleChain(getSourceSection(), frameDescriptor, inlineableRule, dispatchChain));
	}

}
