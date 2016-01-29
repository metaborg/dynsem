package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public class RuleResult {

	public ITerm result;
	public ITerm[] components;
}
