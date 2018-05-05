package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.Node.Child;
import com.oracle.truffle.api.source.SourceSection;

public final class Expanding extends DispatchChainRoot {

	@Child private DispatchChain leftChain;

	@CompilationFinal private int currentOffset;

	@CompilationFinal(dimensions = 1) private final CallTarget[] candidateTargets;
	private final Class<?> dispatchClass;
	private final String arrowName;

	public Expanding(SourceSection source, CallTarget[] candidateTargets, Class<?> dispatchClass, String arrowName,
			boolean failSoftly) {
		super(source, failSoftly);
		this.dispatchClass = dispatchClass;
		this.arrowName = arrowName;
		assert currentOffset < candidateTargets.length;
		this.candidateTargets = candidateTargets;
		this.currentOffset = 0;
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
			CompilerDirectives.transferToInterpreterAndInvalidate();
			currentOffset++;
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
						failSoftly)).executeRight(args);
			}

		}
	}

	protected static Expanding createFromTargets(SourceSection source, CallTarget[] targets, Class<?> dispatchClass,
			String arrowName, boolean failSoftly) {
		return new Expanding(source, targets, dispatchClass, arrowName, failSoftly);
	}

}