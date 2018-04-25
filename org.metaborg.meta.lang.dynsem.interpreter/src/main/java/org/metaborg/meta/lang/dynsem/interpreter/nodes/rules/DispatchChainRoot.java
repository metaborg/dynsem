package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchChainRoot extends DynSemNode {

	protected final boolean failSoftly;

	public DispatchChainRoot(SourceSection source, boolean failSoftly) {
		super(source);
		this.failSoftly = failSoftly;
	}

	public abstract RuleResult execute(Object[] args);

	public static DispatchChainRoot createUninitialized(SourceSection source, String arrowName, Class<?> dispatchClass,
			boolean failSoftly) {
		return new Uninitialized(source, arrowName, dispatchClass, failSoftly);
	}

	public static final class Uninitialized extends DispatchChainRoot {

		private final Class<?> dispatchClass;
		private final String arrowName;

		public Uninitialized(SourceSection source, String arrowName, Class<?> dispatchClass, boolean failSoftly) {
			super(source, failSoftly);
			this.arrowName = arrowName;
			this.dispatchClass = dispatchClass;
		}

		@Override
		public RuleResult execute(Object[] args) {
			CallTarget[] targets = getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass);
			if (targets.length > 0) {
				CompilerDirectives.transferToInterpreterAndInvalidate();
				// System.out.println("Uninitialized -> Expanding (" + dispatchClass.getSimpleName() + ")");
				return replace(
						Expanding.createFromTargets(getSourceSection(), targets, dispatchClass, arrowName, failSoftly))
						.execute(args);
			} else {
				Class<?> nextDispatchClass = DispatchUtils.nextDispatchClass(args[0], dispatchClass);
				if (nextDispatchClass != null) {
					CompilerDirectives.transferToInterpreterAndInvalidate();
					// System.out
					// .println("Uninitialized -> Fallback Uninitialized (" + dispatchClass.getSimpleName() + ")");
					return replace(DispatchChainRoot.createUninitialized(getSourceSection(), arrowName,
							nextDispatchClass, failSoftly)).execute(args);
				}
				if (failSoftly) {
					throw PremiseFailureException.SINGLETON;
				} else {
					throw new ReductionFailure("Rule application failed", InterpreterUtils.createStacktrace(), this);
				}
			}
		}

	}

	public static final class Expanding extends DispatchChainRoot {

		@Child private DispatchChain leftChain;

		@CompilationFinal private int currentOffset;

		private final CallTarget[] candidateTargets;
		private final Class<?> dispatchClass;
		private final String arrowName;

		public Expanding(SourceSection source, int currentOffset, CallTarget[] candidateTargets, Class<?> dispatchClass,
				String arrowName, boolean failSoftly) {
			super(source, failSoftly);
			this.dispatchClass = dispatchClass;
			this.arrowName = arrowName;
			assert currentOffset < candidateTargets.length;
			this.candidateTargets = candidateTargets;
			this.currentOffset = currentOffset;
			this.leftChain = new DispatchChain(source, candidateTargets[currentOffset], null);
		}

		@Override
		public RuleResult execute(Object[] args) {
			try {
				return leftChain.execute(args);
			} catch (PremiseFailureException pmfx) {
				return expandAndTryAgain(args);
			}
		}

		public RuleResult executeFirst(Object[] args) {
			try {
				return leftChain.executeLeft(args);
			} catch (PremiseFailureException pmfx) {
				return expandAndTryAgain(args);
			}
		}

		private RuleResult expandAndTryAgain(Object[] args) {
			if (currentOffset + 1 < candidateTargets.length) {
				// System.out.println("Expanding -> Expanding (" + dispatchClass.getSimpleName() + ")");
				// we can still inject another candidate call target
				currentOffset++;
				CompilerDirectives.transferToInterpreterAndInvalidate();
				leftChain = insert(new DispatchChain(getSourceSection(), candidateTargets[currentOffset], leftChain));
				return executeFirst(args);
			} else {
				// chain is fully expanded, we have to fall back
				CompilerDirectives.transferToInterpreterAndInvalidate();
				// System.out.println("Expanding -> Expanded (" + dispatchClass.getSimpleName() + ")");
				Class<?> nextDispatchClass = DispatchUtils.nextDispatchClass(args[0], dispatchClass);
				if (nextDispatchClass == null) {
					return replace(new Expanded(getSourceSection(), leftChain, null, failSoftly)).executeRight(args);
				} else {
					return replace(new Expanded(getSourceSection(), leftChain,
							new Uninitialized(getSourceSection(), arrowName, nextDispatchClass, failSoftly),
							failSoftly))
									.executeRight(args);
				}

			}
		}

		protected static Expanding createFromTargets(SourceSection source, CallTarget[] targets, Class<?> dispatchClass,
				String arrowName, boolean failSoftly) {
			return new Expanding(source, 0, targets, dispatchClass, arrowName, failSoftly);
		}

	}

	public static class Expanded extends DispatchChainRoot {
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
					throw new ReductionFailure("Reduction failure", InterpreterUtils.createStacktrace(), this);
				}
			}
			return rightChain.execute(args);
		}

	}

}
