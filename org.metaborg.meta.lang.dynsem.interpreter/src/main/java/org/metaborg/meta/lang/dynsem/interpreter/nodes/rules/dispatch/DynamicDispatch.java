package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynamicDispatch extends AbstractDispatch {

	public DynamicDispatch(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Specialization(guards = "termClass(args) == termClass", limit = "3")
	public RuleResult doCaching(Object[] args, @Cached("termClass(args)") Class<?> termClass,
			@Cached("create(lookup(termClass))") DirectCallNode callNode) {
		return (RuleResult) callNode.call(args);
	}

	@Specialization
	public RuleResult doLookup(Object[] args, @Cached("create()") IndirectCallNode callNode) {
		return (RuleResult) callNode.call(lookup(termClass(args)), args);
	}

	protected static Class<?> termClass(Object[] args) {
		return args[0].getClass();
	}

	protected CallTarget lookup(Class<?> termClass) {
		return getContext().getRuleRegistry().lookupCallTarget(arrowName, termClass);
	}

	public static DynamicDispatch create(SourceSection source, String arrowName) {
		return DynamicDispatchNodeGen.create(source, arrowName);
	}

}
