package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.AbstractDispatch;
import org.spoofax.interpreter.terms.IStrategoAppl;

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
			if (dispatchChain == null) {
				return growAndExecute(args);
			}
			if (deepAllowed) {
				return dispatchChain.execute(args);
			} else {
				return dispatchChain.doShallow(args);
			}
		} catch (PremiseFailureException pmfex) {
			return growAndExecute(args);
		}
	}

	protected abstract Class<?> dispatchClass();

	protected abstract WrappedRuleNode createRuleForInlining(DynSemLanguage language, IStrategoAppl ruleSourceTerm,
			FrameDescriptor frameDescriptor);

	private RuleResult growAndExecute(Object[] args) {
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
		WrappedRuleNode inlineableRule = createRuleForInlining(ruleReg.getLanguage(), nextRoot.getRuleSourceTerm(),
				frameDescriptor);
		dispatchChain = insert(
				InlinedRuleChainedNodeGen.create(getSourceSection(), frameDescriptor, inlineableRule, dispatchChain));
		return executeHelper(args, false);
	}

}
