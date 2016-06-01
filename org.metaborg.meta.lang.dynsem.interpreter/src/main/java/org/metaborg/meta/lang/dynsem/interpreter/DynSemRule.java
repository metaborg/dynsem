package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class DynSemRule implements TruffleObject {
	private final RuleRoot ruleTarget;

	public DynSemRule(RuleRoot ruleTarget) {
		this.ruleTarget = ruleTarget;
	}

	@Override
	public ForeignAccess getForeignAccess() {
		return DynSemRuleForeignAccess.INSTANCE;
	}

	/**
	 * @return The {@link RuleRoot} that can be executed using foreign access.
	 */
	public RuleRoot getRuleTarget() {
		return ruleTarget;
	}
}
