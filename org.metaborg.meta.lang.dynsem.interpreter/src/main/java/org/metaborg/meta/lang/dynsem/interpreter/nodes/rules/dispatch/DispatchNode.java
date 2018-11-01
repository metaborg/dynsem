package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PrimaryCachingDispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchNode extends DynSemNode {

	protected final String arrowName;

	public DispatchNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Class<?> dispatchClass, Object[] args);

	public static DispatchNode create(SourceSection source, String arrowName) {
		return PrimaryCachingDispatchNodeGen.create(source, arrowName);
	}
}
