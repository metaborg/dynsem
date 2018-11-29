package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.AbstractDispatch;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InliningDispatchNode extends AbstractDispatch {

	@Child protected InlinedRuleChainedNode dispatchChain;

	public InliningDispatchNode(SourceSection source, String arrowName) {
		super(source, arrowName);
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

	protected abstract Class<?> dispatchClass();

	protected abstract RuleNode createRuleForInlining(DynSemLanguage language, IStrategoAppl ruleSourceTerm,
			FrameDescriptor frameDescriptor, ITermRegistry termReg);

	private void growChain() {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		IRuleRegistry ruleReg = getContext().getRuleRegistry();
		RuleRootNode[] roots = ruleReg.lookupRuleRoots(arrowName, dispatchClass());
		int currentChainLength = dispatchChain != null ? dispatchChain.length() : 0;

		if (currentChainLength >= roots.length) {
			// FIXME: of course, we should attempt a sort-wide rule
			throw new RuntimeException("No more rules to try. And sort-dispatch is NOT IMPLEMENTED");
		}

		RuleRootNode nextRoot = roots[currentChainLength];
		FrameDescriptor frameDescriptor = nextRoot.getFrameDescriptor();
		RuleNode inlineableRule = createRuleForInlining(ruleReg.getLanguage(), nextRoot.getRuleSourceTerm(),
				frameDescriptor, getContext().getTermRegistry());
		this.dispatchChain = insert(
				new InlinedRuleChainedNode(getSourceSection(), frameDescriptor, inlineableRule, dispatchChain));
	}

}
