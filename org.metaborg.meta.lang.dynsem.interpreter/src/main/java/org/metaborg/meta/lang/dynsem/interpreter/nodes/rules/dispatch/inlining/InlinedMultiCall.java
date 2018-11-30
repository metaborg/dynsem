package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.graalvm.collections.EconomicSet;
import org.graalvm.collections.Equivalence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class InlinedMultiCall extends InlinedCall {
	@Child protected IndirectCallNode callNode;
	@Child protected InlinedCallChainItem chain;

	private final EconomicSet<CallTarget> remainingTargets;

	public InlinedMultiCall(SourceSection source, CallTarget[] targets) {
		super(source);
		this.callNode = IndirectCallNode.create();
		this.remainingTargets = EconomicSet.create(Equivalence.IDENTITY);
		for (CallTarget target : targets) {
			this.remainingTargets.add(target);
		}
	}

	@Override
	public RuleResult execute(Object[] callArgs) {
		try {
			if (chain == null) {
				return fetchExecuteAndGrow(callArgs);
			} else {
				return chain.execute(callArgs);
			}
		} catch (PremiseFailureException pmfex) {
			return fetchExecuteAndGrow(callArgs);
		}
	}

	private RuleResult fetchExecuteAndGrow(Object[] callArgs) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		for (CallTarget candidateTarget : remainingTargets) {
			try {
				RuleResult result = (RuleResult) callNode.call(candidateTarget, callArgs);
				remainingTargets.remove(candidateTarget);
				chain = insert(
						new InlinedCallChainItem(getSourceSection(), createInlineableRule(candidateTarget), chain));
				return result;
			} catch (PremiseFailureException pmfex) {
				continue;
			}
		}
		throw new ReductionFailure("No more rules to try. And sort-dispatch is NOT IMPLEMENTED",
				InterpreterUtils.createStacktrace(), this);
	}

	public static final class InlinedCallChainItem extends DynSemNode {

		@Child protected InlinedRuleWrap inlinedRule;
		@Child protected InlinedCallChainItem next;

		public InlinedCallChainItem(SourceSection source, InlinedRuleWrap inlinedRule, InlinedCallChainItem next) {
			super(source);
			this.inlinedRule = inlinedRule;
			this.next = next;
		}

		private final BranchProfile nextEntered = BranchProfile.create();

		public RuleResult execute(Object[] callArgs) {
			try {
				return inlinedRule.execute(callArgs);
			} catch (PremiseFailureException pmfex) {
				if (next != null) {
					// if (CompilerDirectives.injectBranchProbability(CompilerDirectives.UNLIKELY_PROBABILITY, next !=
					// null)) {
					nextEntered.enter();
					return next.execute(callArgs);
				}
				throw pmfex;
			}
		}

	}

}
