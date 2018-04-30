package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public class RuleResult {
	public Object result;
	@CompilationFinal(dimensions = 0) public final Object[] components;

	public RuleResult(Object[] components) {
		this.components = components;
	}

}
