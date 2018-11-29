package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public final class DynamicDispatchNode extends AbstractDispatch {

	@Child protected IndirectCallNode callNode = IndirectCallNode.create();

	public DynamicDispatchNode(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Override
	public RuleResult execute(Object[] args) {
		CallTarget[] targets = getContext().getRuleRegistry().lookupCallTargets(arrowName, args[0].getClass());
		for (CallTarget target : targets) {
			try {
				return (RuleResult) callNode.call(target, args);
			} catch (PremiseFailureException pmfex) {
				;
			}
		}
		throw new ReductionFailure("Reduction failed on arrow -" + arrowName + "-> for term " + args[0],
				InterpreterUtils.createStacktrace(), this);
	}

	public static final DynamicDispatchNode create(SourceSection source, String arrowName) {
		return new DynamicDispatchNode(source, arrowName);
	}

}
