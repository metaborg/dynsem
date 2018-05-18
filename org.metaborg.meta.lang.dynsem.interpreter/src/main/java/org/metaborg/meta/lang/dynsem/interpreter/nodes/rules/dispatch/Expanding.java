package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import java.util.Iterator;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.CircularBuffer;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public final class Expanding extends DispatchChainRoot {
	private final Class<?> dispatchClass;
	private final String arrowName;
	private final CircularBuffer<CallTarget> targetBuffer;

	@Child private DispatchChain leftChain;
	@Child private IndirectCallNode indirectCallNode;

	@Child private DispatchChainRoot rightChain;
	@CompilationFinal private Class<?> nextDispatchClass;
	@CompilationFinal private boolean hasExecutedRight;

	public Expanding(SourceSection source, CallTarget[] candidateTargets, Class<?> dispatchClass, String arrowName,
			boolean failSoftly) {
		super(source, failSoftly);
		this.dispatchClass = dispatchClass;
		this.arrowName = arrowName;
		assert candidateTargets.length > 0;
		this.indirectCallNode = IndirectCallNode.create();
		this.targetBuffer = new CircularBuffer<>(candidateTargets);
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
		// if no more candidates, replace with fully expanded
		if (targetBuffer.size() == 0) {
			// no more candidates in buffer
			assert leftChain != null;
			return replace(new Expanded(getSourceSection(), leftChain, null, failSoftly)).executeRight(args);
		}

		// there are more candidates, try them
		Iterator<CallTarget> candidateIter = targetBuffer.iterator();
		while (candidateIter.hasNext()) {
			CallTarget candidate = candidateIter.next();
			try {
				RuleResult result = (RuleResult) indirectCallNode.call(candidate, args);
				// call succeeded, move from buffer to chain
				candidateIter.remove();
				CompilerDirectives.transferToInterpreterAndInvalidate();
				leftChain = insert(new DispatchChain(getSourceSection(), candidate, leftChain));
				return result;
			} catch (PremiseFailureException pmfx) {
				;
			}
		}

		// everything has failed, we need to try fallback
		if (!hasExecutedRight) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			nextDispatchClass = DispatchUtils.nextDispatchClass(args[0], dispatchClass);
			if (nextDispatchClass != null) {
				rightChain = insert(new Uninitialized(getSourceSection(), arrowName, nextDispatchClass, failSoftly));
			}

		}

		if (nextDispatchClass == null) {
			if (failSoftly) {
				throw PremiseFailureException.SINGLETON;
			} else {
				throw new ReductionFailure("No more rules to try", InterpreterUtils.createStacktrace(), this);
			}
		} else {
			return rightChain.execute(args);
		}
	}

	protected static Expanding createFromTargets(SourceSection source, CallTarget[] targets, Class<?> dispatchClass,
			String arrowName, boolean failSoftly) {
		return new Expanding(source, targets, dispatchClass, arrowName, failSoftly);
	}

}