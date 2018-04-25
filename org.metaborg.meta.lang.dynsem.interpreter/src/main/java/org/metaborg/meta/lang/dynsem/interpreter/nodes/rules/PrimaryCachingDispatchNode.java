package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class PrimaryCachingDispatchNode extends DispatchNode {

	public PrimaryCachingDispatchNode(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Override
	public abstract RuleResult execute(Class<?> dispatchClass, Object[] args);

	@Specialization(limit = "1", guards = { "dispatchClass == cachedDispatchClass" })
	public RuleResult doCachedPrimary(Class<?> dispatchClass, Object[] args,
			@Cached("dispatchClass") Class<?> cachedDispatchClass,
			@Cached("createDispatchChain(cachedDispatchClass)") DispatchChainRoot dispatchChain) {
		return dispatchChain.execute(args);
	}

	@Specialization(replaces = "doCachedPrimary")
	public RuleResult doSecondary(Class<?> dispatchClass, Object[] args,
			@Cached("createSecondaryDispatch()") SecondaryCachingDispatchNode secondaryDispatch) {
		return secondaryDispatch.execute(dispatchClass, args);
	}

	protected DispatchChainRoot createDispatchChain(Class<?> dispatchClass) {
		return DispatchChainRoot.createUninitialized(getSourceSection(), arrowName, dispatchClass, false);
	}

	protected SecondaryCachingDispatchNode createSecondaryDispatch() {
		return SecondaryCachingDispatchNode.createUninitialized(getSourceSection(), arrowName);
	}

	// @Specialization(limit = "4", guards = "dispatchClass == cachedDispatchClass")
	// public RuleResult doDirect(Class<?> dispatchClass, Object[] args,
	// @Cached("dispatchClass") Class<?> cachedDispatchClass,
	// @Cached("create(getUnifiedCallTarget(cachedDispatchClass))") DirectCallNode callNode) {
	// return (RuleResult) callNode.call(args);
	// }
	//
	// @Specialization(replaces = "doDirect")
	// public RuleResult doIndirect(Class<?> dispatchClass, Object[] args, @Cached("create()") IndirectCallNode
	// callNode) {
	// // printmiss(dispatchClass);
	// return (RuleResult) callNode.call(getUnifiedCallTarget(dispatchClass), args);
	// }
	//
	// // @TruffleBoundary
	// // private void printmiss(Class<?> dispatchClass) {
	// // System.out.println("Cache miss dispatching on " + dispatchClass.getSimpleName() + " from " + getRootNode());
	// // }
	//
	// protected final CallTarget getUnifiedCallTarget(Class<?> dispatchClass) {
	// return getContext().getRuleRegistry().lookupRule(arrowName, dispatchClass);
	// }
	//


}
