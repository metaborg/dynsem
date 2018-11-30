package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.AbstractDispatch;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InlinedDispatch extends AbstractDispatch {

	public InlinedDispatch(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Specialization
	public RuleResult doCached(Object[] callArgs,
			@Cached("create(getSourceSection(), lookupTargets(termClass(callArgs)))") InlinedCall callNode) {
		return callNode.execute(callArgs);
	}

	protected static Class<?> termClass(Object[] args) {
		return args[0].getClass();
	}

	protected CallTarget[] lookupTargets(Class<?> termClass) {
		return getContext().getRuleRegistry().lookupCallTargets(arrowName, termClass);
	}

	public static InlinedDispatch create(SourceSection source, String arrowName) {
		return InlinedDispatchNodeGen.create(source, arrowName);
	}
}
