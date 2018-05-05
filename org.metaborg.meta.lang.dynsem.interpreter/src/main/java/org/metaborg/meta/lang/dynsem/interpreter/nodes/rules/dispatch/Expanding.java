package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public final class Expanding extends DispatchChainRoot {

	@Child private DispatchChain leftChain;
	@Child private IndirectCallNode tryoutCallNode;

	private final List<CallTarget> candidateTargets;

	// @CompilationFinal(dimensions = 1) private final CallTarget[] candidateTargets;
	private final Class<?> dispatchClass;
	private final String arrowName;

	public Expanding(SourceSection source, CallTarget[] candidateTargets, Class<?> dispatchClass, String arrowName,
			boolean failSoftly) {
		super(source, failSoftly);
		this.dispatchClass = dispatchClass;
		this.arrowName = arrowName;
		Arrays.asList(candidateTargets);
		this.candidateTargets = new ArrayList<>(
				Arrays.asList(Arrays.copyOf(candidateTargets, candidateTargets.length)));
		// this.leftChain = new DispatchChain(source, this.candidateTargets.pop(), null);
		this.leftChain = null;
		this.tryoutCallNode = IndirectCallNode.create();
	}

	@Override
	public RuleResult execute(Object[] args) {
		if (leftChain == null) {
			return expandAndTryAgain(args);
		}
		try {
			return leftChain.execute(args);
		} catch (PremiseFailureException pmfx) {
			return expandAndTryAgain(args);
		}
	}

	// public RuleResult executeFirst(Object[] args) {
	// try {
	// return leftChain.executeLeft(args);
	// } catch (PremiseFailureException pmfx) {
	// return expandAndTryAgain(args);
	// }
	// }
	@TruffleBoundary
	private boolean moreCandidates() {
		return !candidateTargets.isEmpty();
	}

	@TruffleBoundary
	private CallTarget[] getAllCandidates() {
		return candidateTargets.toArray(new CallTarget[candidateTargets.size()]);
	}

	@TruffleBoundary
	private void removeCandidate(int idx) {
		candidateTargets.remove(idx);
	}

	private RuleResult expandAndTryAgain(Object[] args) {
		if(moreCandidates()) {
			RuleResult res = null;
			CallTarget successFullTarget = null;
			for (CallTarget target : candidateTargets) {
				try {
					res = (RuleResult) tryoutCallNode.call(target, args);
					successFullTarget = target;
					break;
				} catch (PremiseFailureException pmfx) {
					;
				}
			}
			if (res != null) {
				// we have found a successful candidate
				// removeCandidate(idx);
				CompilerDirectives.transferToInterpreterAndInvalidate();
				candidateTargets.remove(successFullTarget);
				this.leftChain = insert(new DispatchChain(getSourceSection(), successFullTarget, this.leftChain));
				return res;
			}
		}
		throw new IllegalStateException("Rule fallback not implemented");
	}

	// private RuleResult expandAndTryAgain(Object[] args) {
	// if (currentOffset + 1 < candidateTargets.length) {
	// // System.out.println("Expanding -> Expanding (" + dispatchClass.getSimpleName() + ")");
	// // we can still inject another candidate call target
	// CompilerDirectives.transferToInterpreterAndInvalidate();
	// currentOffset++;
	// leftChain = insert(new DispatchChain(getSourceSection(), candidateTargets[currentOffset], leftChain));
	// InterpreterUtils.printlnStdOut("Expanded. New length: " + leftChain.size());
	// return executeFirst(args);
	// } else {
	// // chain is fully expanded, we have to fall back
	// CompilerDirectives.transferToInterpreterAndInvalidate();
	// // System.out.println("Expanding -> Expanded (" + dispatchClass.getSimpleName() + ")");
	// Class<?> nextDispatchClass = DispatchUtils.nextDispatchClass(args[0], dispatchClass);
	// if (nextDispatchClass == null) {
	// return replace(new Expanded(getSourceSection(), leftChain, null, failSoftly)).executeRight(args);
	// } else {
	// return replace(new Expanded(getSourceSection(), leftChain,
	// new Uninitialized(getSourceSection(), arrowName, nextDispatchClass, failSoftly),
	// failSoftly)).executeRight(args);
	// }
	//
	// }
	// }

	protected static Expanding createFromTargets(SourceSection source, CallTarget[] targets, Class<?> dispatchClass,
			String arrowName, boolean failSoftly) {
		return new Expanding(source, targets, dispatchClass, arrowName, failSoftly);
	}

}