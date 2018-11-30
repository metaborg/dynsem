package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.calls;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public class DirectSingleCall extends DirectCall {

	@Child protected DirectCallNode callNode;

	public DirectSingleCall(SourceSection source, CallTarget target) {
		super(source);
		callNode = DirectCallNode.create(target);
	}

	@Override
	public RuleResult execute(Object[] callArgs) {
		return (RuleResult) callNode.call(callArgs);
	}

}
