package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public class RuleResult {

	public IStrategoTerm result;
	public IStrategoTerm[] components;
}
