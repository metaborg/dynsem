package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public final class Uninitialized extends DispatchChainRoot {

	@Child private DispatchChainRoot chain;

	private final Class<?> dispatchClass;
	private final String arrowName;

	public Uninitialized(SourceSection source, String arrowName, Class<?> dispatchClass, boolean failSoftly) {
		super(source, failSoftly);
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
	}

	@Override
	public RuleResult execute(Object[] args) {
		if (chain == null) {
			CallTarget[] targets = getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass);
			if (targets.length > 0) {
				CompilerDirectives.transferToInterpreterAndInvalidate();
				this.chain = insert(Expanding.createFromTargets(getSourceSection(), targets, dispatchClass,
						arrowName, failSoftly));
			} else {
				Class<?> nextDispatchClass = DispatchUtils.nextDispatchClass(args[0], dispatchClass);
				if (nextDispatchClass != null) {
					CompilerDirectives.transferToInterpreterAndInvalidate();
					this.chain = insert(DispatchChainRoot.createUninitialized(getSourceSection(), arrowName,
							nextDispatchClass, failSoftly));
				} else {
					if (failSoftly) {
						throw PremiseFailureException.SINGLETON;
					} else {
						throw new ReductionFailure(
								"No rules applicable for " + dispatchClass.getSimpleName() + " on " + args[0],
								InterpreterUtils.createStacktrace(), this);
					}
				}
			}
		}
		return chain.execute(args);
	}

	@TruffleBoundary
	@Override
	public String toString() {
		return "Uninitialized[" + dispatchClass.getSimpleName() + "](" + chain + ")";
	}

}