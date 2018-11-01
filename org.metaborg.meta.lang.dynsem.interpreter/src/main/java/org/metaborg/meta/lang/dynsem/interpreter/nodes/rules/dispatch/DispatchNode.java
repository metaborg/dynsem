package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PrimaryCachingDispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchNode extends AbstractDispatch {


	public DispatchNode(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Override
	public final RuleResult execute(Object[] args) {
		return execute(args[0].getClass(), args);
	}

	public abstract RuleResult execute(Class<?> dispatchClass, Object[] args);

	public static DispatchNode create(SourceSection source, String arrowName) {
		return PrimaryCachingDispatchNodeGen.create(source, arrowName);
	}
}
