package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.SecondaryCachingDispatchNodeFactory.GenericDispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.SecondaryCachingDispatchNodeFactory.WithFailureNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.SecondaryCachingDispatchNodeFactory.WithoutFailureNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchChainRoot;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

@ImportStatic(DispatchUtils.class)
public abstract class SecondaryCachingDispatchNode extends DispatchNode {

	public SecondaryCachingDispatchNode(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	public static SecondaryCachingDispatchNode createUninitialized(SourceSection source, String arrowName) {
		return WithoutFailureNodeGen.create(source, arrowName);
	}

	protected GenericDispatch createGenericDispatch() {
		return GenericDispatchNodeGen.create(getSourceSection(), arrowName);
	}

	protected DispatchChainRoot createRootDispatch(Class<?> dispatchClass) {
		return DispatchChainRoot.createUninitialized(getSourceSection(), arrowName, dispatchClass, false);
	}

	public static abstract class GenericDispatch extends DispatchNode {

		public GenericDispatch(SourceSection source, String arrowName) {
			super(source, arrowName);
		}

		@Specialization
		// @ExplodeLoop
		public RuleResult executeNoFailure(Class<?> dispatchClass, Object[] args,
				@Cached("create()") IndirectCallNode callNode) {
			CallTarget[] callTargets = getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass);
			for (int i = 0; i < callTargets.length; i++) {
				try {
					return (RuleResult) callNode.call(callTargets[i], args);
				} catch (PremiseFailureException pmfx) {
					;
				}
			}

			throw PremiseFailureException.SINGLETON;
		}
	}

	public static abstract class WithoutFailure extends SecondaryCachingDispatchNode {

		public WithoutFailure(SourceSection source, String arrowName) {
			super(source, arrowName);
		}

		@Specialization
		public RuleResult executeNoFailure(Class<?> dispatchClass, Object[] args,
				@Cached("createGenericDispatch()") GenericDispatch left) {
			try {
				return left.execute(dispatchClass, args);
			} catch (PremiseFailureException pmfx) {
				// System.out.println("WithoutFailure -> WithFailure (" + dispatchClass.getName() + ")");
				CompilerDirectives.transferToInterpreterAndInvalidate();
				return replace(WithFailureNodeGen.create(getSourceSection(), arrowName, left))
						.executeSkippingLeft(dispatchClass, args, true);
			}
		}

	}

	public static abstract class WithFailure extends SecondaryCachingDispatchNode {

		@Child private DispatchNode left;

		public WithFailure(SourceSection source, String arrowName, DispatchNode left) {
			super(source, arrowName);
			this.left = left;
		}

		@Override
		public final RuleResult execute(Class<?> dispatchClass, Object[] args) {
			return executeSkippingLeft(dispatchClass, args, false);
		}

		public abstract RuleResult executeSkippingLeft(Class<?> dispatchClass, Object[] args, boolean skipLeft);

		@Specialization(guards = { "nextDispatchClass(args, dispatchClass) == cachedNextDispatchClass",
				"cachedNextDispatchClass != null" })
		public RuleResult executeCachedNotNullRight(Class<?> dispatchClass, Object[] args, boolean skipLeft,
				@Cached("nextDispatchClass(args, dispatchClass)") Class<?> cachedNextDispatchClass,
				@Cached("createRootDispatch(cachedNextDispatchClass)") DispatchChainRoot right) {
			if (!skipLeft) {
				try {
					return left.execute(dispatchClass, args);
				} catch (PremiseFailureException pmfx) {
					;
				}
			}

			return right.execute(args);
		}

		@Specialization(guards = { "nextDispatchClass(args, dispatchClass) == cachedNextDispatchClass",
				"cachedNextDispatchClass == null" })
		public RuleResult executeCachedNullRight(Class<?> dispatchClass, Object[] args, boolean skipLeft,
				@Cached("nextDispatchClass(args, dispatchClass)") Class<?> cachedNextDispatchClass) {
			if (!skipLeft) {
				try {
					return left.execute(dispatchClass, args);
				} catch (PremiseFailureException pmfx) {
					;
				}
			}

			throw new ReductionFailure("Reduction failed", InterpreterUtils.createStacktrace(), this);
		}

		@Specialization(replaces = { "executeCachedNotNullRight", "executeCachedNullRight" })
		public RuleResult executeGeneric(Class<?> dispatchClass, Object[] args, boolean skipLeft,
				@Cached("createGenericDispatch()") GenericDispatch right) {
			if (!skipLeft) {
				try {
					return left.execute(dispatchClass, args);
				} catch (PremiseFailureException pmfx) {
					;
				}
			}
			Class<?> nextDispatchClass = DispatchUtils.nextDispatchClass(args, dispatchClass);
			if (nextDispatchClass != null) {
				try {
					return right.execute(dispatchClass, args);
				} catch (PremiseFailureException pmfx) {
					;
				}
			}

			throw new ReductionFailure("Reduction failed", InterpreterUtils.createStacktrace(), this);
		}

	}
}
