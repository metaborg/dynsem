package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleUnionRoot;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class DynSemRule implements TruffleObject {

	private final RuleUnionRoot ruleTarget;

	public DynSemRule(RuleUnionRoot ruleTarget) {
		this.ruleTarget = ruleTarget;
	}

	@Override
	public ForeignAccess getForeignAccess() {
		return DynSemRuleForeignAccess.INSTANCE;
	}

	/**
	 * @return The {@link RuleRoot} that can be executed using foreign access.
	 */
	public RuleUnionRoot getRuleTarget() {
		return ruleTarget;
	}
}
