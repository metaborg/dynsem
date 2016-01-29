package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public class RuleResult {
	public Object result;
	public Object[] components;
}
