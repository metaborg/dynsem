package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public final class DispatchChain extends DynSemNode {

	@Child private DirectCallNode leftCall;
	@Child private DispatchChain right;

	public DispatchChain(SourceSection source, CallTarget leftTarget, DispatchChain right) {
		super(source);
		this.leftCall = DirectCallNode.create(leftTarget);
		this.right = right;
	}

	private final BranchProfile rightTaken = BranchProfile.create();

	public RuleResult execute(Object[] args) {
		try {
			return (RuleResult) leftCall.call(args);
		} catch (PremiseFailureException pfx) {
			rightTaken.enter();
			if (right != null) {
				return right.execute(args);
			} else {
				throw pfx;
			}
		}
	}

	public RuleResult executeLeft(Object[] args) {
		return (RuleResult) leftCall.call(args);
	}

}
