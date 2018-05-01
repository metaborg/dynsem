package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class PrimaryCachingDispatchNode extends DispatchNode {

	public PrimaryCachingDispatchNode(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Specialization(limit = "4", guards = { "dispatchKey == cachedDispatchKey" })
	public RuleResult doCachedPrimary(String dispatchKey, Object[] args,
			@Cached("dispatchKey") String cachedDispatchKey,
			@Cached("createDispatchChain(cachedDispatchKey)") DispatchChainRoot dispatchChain) {
		return dispatchChain.execute(args);
	}

	@Specialization(replaces = "doCachedPrimary")
	public RuleResult doSecondary(String dispatchKey, Object[] args,
			@Cached("createSecondaryDispatch()") SecondaryCachingDispatchNode secondaryDispatch) {
		return secondaryDispatch.execute(dispatchKey, args);
	}

	@TruffleBoundary
	protected static boolean stringEq(String a, String b) {
		return a.equals(b);
	}

	protected DispatchChainRoot createDispatchChain(String dispatchKey) {
		return DispatchChainRoot.createUninitialized(getSourceSection(), arrowName, dispatchKey, false);
	}

	protected SecondaryCachingDispatchNode createSecondaryDispatch() {
		return SecondaryCachingDispatchNode.createUninitialized(getSourceSection(), arrowName);
	}



}
