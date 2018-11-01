package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.AbstractDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public final class ConstantDispatchNode extends AbstractDispatch {

	private final Object inputTerm;

	@Child protected InlinedDispatchChainedNode dispatchChain;

	public ConstantDispatchNode(SourceSection source, Object inputTerm, String arrowName) {
		super(source, arrowName);
		this.inputTerm = inputTerm;
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

	private RuleResult growAndExecute(Object[] args) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		IRuleRegistry ruleReg = getContext().getRuleRegistry();
		RuleRootNode[] roots = ruleReg.lookupRuleRoots(arrowName, inputTerm.getClass());
		int currentChainLength = dispatchChain != null ? dispatchChain.length() : 0;

		if (currentChainLength >= roots.length) {
			// FIXME: of course, we should attempt a sort-wide rule
			throw new RuntimeException("No more rules to try");
		}

		RuleRootNode nextRoot = roots[currentChainLength];
		FrameDescriptor frameDescriptor = nextRoot.getFrameDescriptor();
		RuleNode clonedRule = RuleNode.create(ruleReg.getLanguage(), nextRoot.getRuleSourceTerm(), frameDescriptor);
		dispatchChain = insert(
				InlinedDispatchChainedNodeGen.create(getSourceSection(), frameDescriptor, clonedRule, dispatchChain));
		return executeHelper(args, false);
	}

	public static ConstantDispatchNode create(Object inputTerm, String arrowName) {
		return new ConstantDispatchNode(SourceUtils.dynsemSourceSectionUnvailable(), inputTerm, arrowName);
	}

}
