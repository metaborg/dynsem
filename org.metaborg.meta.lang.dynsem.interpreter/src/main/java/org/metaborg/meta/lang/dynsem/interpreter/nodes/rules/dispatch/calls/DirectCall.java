package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.calls;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DirectCall extends DynSemNode {

	public DirectCall(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(Object[] callArgs);

	public static DirectCall create(SourceSection source, CallTarget[] targets) {
		if (targets.length > 1) {
			return new DirectMultiCall(source, targets);
		} else {
			return new DirectSingleCall(source, targets[0]);
		}
	}

}
