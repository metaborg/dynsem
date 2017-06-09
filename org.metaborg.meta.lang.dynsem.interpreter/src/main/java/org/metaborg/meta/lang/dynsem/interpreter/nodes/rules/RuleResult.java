package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public class RuleResult {
	public final Object result;
	public final Object[] components;

	public RuleResult(Object result, Object[] components) {
		this.result = result;
		this.components = components;
	}

}
