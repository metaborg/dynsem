package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class Expanded extends DispatchChainRoot {
	@Child private DispatchChain leftChain;
	@Child private DispatchChainRoot rightChain;

	public Expanded(SourceSection source, DispatchChain leftChain, DispatchChainRoot rightChain,
			boolean failSoftly) {
		super(source, failSoftly);
		this.leftChain = leftChain;
		this.rightChain = rightChain;
	}

	private final BranchProfile rightTaken = BranchProfile.create();

	@Override
	public RuleResult execute(Object[] args) {
		try {
			return leftChain.execute(args);
		} catch (PremiseFailureException pmfx) {
			rightTaken.enter();
			return executeRight(args);
		}
	}

	public RuleResult executeRight(Object[] args) {
		if (rightChain == null) {
			if (failSoftly) {
				throw PremiseFailureException.SINGLETON;
			} else {
				throw new ReductionFailure("No more rules to try", InterpreterUtils.createStacktrace(), this);
			}
		}
		return rightChain.execute(args);
	}
	
	@TruffleBoundary
	@Override
	public String toString() {
		return "Expanded(left: " + leftChain + ", right: " + rightChain + ")";
	}

}