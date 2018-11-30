package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.calls;

import org.graalvm.collections.EconomicSet;
import org.graalvm.collections.Equivalence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class DirectMultiCall extends DirectCall {

	@Child protected DirectCallChainItem chain;

	@Child protected IndirectCallNode callNode;

	private final EconomicSet<CallTarget> remainingTargets;

	public DirectMultiCall(SourceSection source, CallTarget[] targets) {
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
				chain = insert(new DirectCallChainItem(getSourceSection(), candidateTarget, chain));
				return result;
			} catch (PremiseFailureException pmfex) {
				continue;
			}
		}
		throw new ReductionFailure("No more rules to try. And sort-dispatch is NOT IMPLEMENTED",
				InterpreterUtils.createStacktrace(), this);
	}

	public static final class DirectCallChainItem extends DynSemNode {

		@Child protected DirectCallNode callNode;
		@Child protected DirectCallChainItem next;

		public DirectCallChainItem(SourceSection source, CallTarget target, DirectCallChainItem next) {
			super(source);
			this.callNode = DirectCallNode.create(target);
			this.next = next;
		}

		private final BranchProfile nextEntered = BranchProfile.create();

		public RuleResult execute(Object[] callArgs) {
			try {
				return (RuleResult) callNode.call(callArgs);
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
