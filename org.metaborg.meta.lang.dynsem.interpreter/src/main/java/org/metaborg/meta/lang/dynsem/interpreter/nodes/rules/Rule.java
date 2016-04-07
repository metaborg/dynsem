package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends DynSemNode {

	public Rule(SourceSection sourceSection) {
		super(sourceSection);
	}

	public abstract int getArity();

	public abstract String getConstructor();

	public abstract String getName();

	public abstract RuleResult execute(VirtualFrame frame);

	@Deprecated
	public static Object[] buildArguments(Object reductionTerm, Object[] comps) {
		CompilerAsserts.compilationConstant(comps.length);

		Object[] args = new Object[1 + comps.length];

		args[0] = reductionTerm;

		System.arraycopy(comps, 0, args, 1, comps.length);

		return args;
	}

}