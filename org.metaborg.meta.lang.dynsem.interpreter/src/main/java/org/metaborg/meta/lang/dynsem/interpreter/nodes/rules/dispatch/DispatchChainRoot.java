package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

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

}
