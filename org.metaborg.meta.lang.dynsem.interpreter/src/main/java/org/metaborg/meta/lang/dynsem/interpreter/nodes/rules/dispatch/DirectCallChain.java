package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public class DirectCallChain extends DynSemNode {

	@CompilationFinal(dimensions = 1) private final CallTarget[] targets;
	@Child protected DirectCallChainItem chain;

	public DirectCallChain(SourceSection source, CallTarget[] targets) {
		super(source);
		this.targets = targets;
	}

	public RuleResult execute(Object[] callArgs) {
		return executeHelper(callArgs, true);
	}

	private RuleResult executeHelper(Object[] args, boolean deepAllowed) {
		// FIXME: only grow chain with those rules which succeed.
		try {
			CompilerAsserts.compilationConstant(chain);
			if (chain == null) {
				growChain();
			}
			return chain.execute(args, deepAllowed);
		} catch (PremiseFailureException pmfex) {
			growChain();
			return executeHelper(args, false);
		}
	}

	private void growChain() {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		int currentChainLength = chain != null ? chain.length() : 0;

		if (currentChainLength >= targets.length) {
			// FIXME: of course, we should attempt a sort-wide rule
			throw new ReductionFailure("No more rules to try. And sort-dispatch is NOT IMPLEMENTED",
					InterpreterUtils.createStacktrace(), this);
		}
		chain = insert(new DirectCallChainItem(getSourceSection(), targets[currentChainLength], chain));
	}

	public static DirectCallChain create(SourceSection source, CallTarget[] targets) {
		return new DirectCallChain(source, targets);
	}

	public static final class DirectCallChainItem extends DynSemNode {

		@Child protected DirectCallNode callNode;
		@Child protected DirectCallChainItem next;

		public DirectCallChainItem(SourceSection source, CallTarget target, DirectCallChainItem next) {
			super(source);
			this.callNode = DirectCallNode.create(target);
			this.next = next;
		}

		public RuleResult execute(Object[] callArgs, boolean deepExecAllowed) {
			try {
				return (RuleResult) callNode.call(callArgs);
			} catch (PremiseFailureException pmfex) {
				if (next != null && deepExecAllowed) {
					return next.execute(callArgs, true);
				}
				throw pmfex;
			}
		}

		protected final int length() {
			if (next == null) {
				return 1;
			} else {
				return 1 + next.length();
			}
		}

	}
}
