package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends Node {

	public Rule(SourceSection sourceSection) {
		super(sourceSection);
	}

	public abstract int getArity();

	public abstract String getConstructor();

	public abstract String getName();

	public abstract RuleResult execute(VirtualFrame frame);

	@Deprecated
	public static Object[] buildArguments(Object reductionTerm,
			Object[] children, Object[] ros, Object[] rws) {

		Object[] args = new Object[1 + children.length + ros.length
				+ rws.length];
		args[0] = reductionTerm;
		System.arraycopy(children, 0, args, 1, children.length);
		System.arraycopy(ros, 0, args, children.length + 1, ros.length);
		System.arraycopy(rws, 0, args, children.length + 1 + ros.length,
				rws.length);

		return args;
	}

	@Deprecated
	public static Object[] buildArguments(Object reductionTerm,
			Object[] children, Object[] components) {
		return buildArguments(reductionTerm, children, new Object[0],
				components);
	}

}