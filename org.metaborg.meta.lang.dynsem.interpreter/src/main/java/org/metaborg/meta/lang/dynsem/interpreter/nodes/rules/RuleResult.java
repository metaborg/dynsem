package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public final class RuleResult {
	public final Object result;
	@CompilationFinal(dimensions = 1) public final Object[] components;

	public RuleResult(Object result, Object[] components) {
		this.result = result;
		this.components = components;
	}

}
