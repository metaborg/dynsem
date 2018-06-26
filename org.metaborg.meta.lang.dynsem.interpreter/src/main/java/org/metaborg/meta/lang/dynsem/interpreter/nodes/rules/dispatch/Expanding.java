package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public final class Expanding extends DispatchChainRoot {

	private final Class<?> dispatchClass;
	private final String arrowName;

	@CompilationFinal(dimensions = 1) private final CallTarget[] candidates;
	private int nextCandidateIndex;

	@Child private DispatchChain leftChain;

	@CompilationFinal private Class<?> nextDispatchClass;

	public Expanding(SourceSection source, CallTarget[] candidateTargets, Class<?> dispatchClass, String arrowName,
			boolean failSoftly) {
		super(source, failSoftly);
		this.dispatchClass = dispatchClass;
		this.arrowName = arrowName;
		assert candidateTargets.length > 0;
		this.candidates = candidateTargets;
		assert this.candidates.length > 0;
		this.nextCandidateIndex = 0;
	}

	@Override
	public RuleResult execute(Object[] args) {
		if (leftChain == null) {
			return expand(args);
		}

		try {
			return leftChain.execute(args);
		} catch (PremiseFailureException pmfx) {
			return expand(args);
		}
	}

	private RuleResult expand(Object[] args) {
		while (nextCandidateIndex < candidates.length) {
			CallTarget candidate = candidates[nextCandidateIndex];
			nextCandidateIndex++;
			leftChain = insert(new DispatchChain(getSourceSection(), candidate, leftChain));
			try {
				return leftChain.executeLeft(args);
			} catch (PremiseFailureException pmfx) {
				;
			}
		}

		assert nextCandidateIndex == candidates.length;
		DispatchChainRoot rightChain = null;
		nextDispatchClass = DispatchUtils.nextDispatchClass(args[0], dispatchClass);
		if (nextDispatchClass != null) {
			rightChain = new Uninitialized(getSourceSection(), arrowName, nextDispatchClass, failSoftly);
		}
		return replace(new Expanded(getSourceSection(), leftChain, rightChain, failSoftly)).executeRight(args);
	}

	@TruffleBoundary
	@Override
	public String toString() {
		return "Expanding[" + dispatchClass.getSimpleName() + "](left: " + leftChain + ")";
	}

	protected static Expanding createFromTargets(SourceSection source, CallTarget[] targets, Class<?> dispatchClass,
			String arrowName, boolean failSoftly) {
		return new Expanding(source, targets, dispatchClass, arrowName, failSoftly);
	}

}