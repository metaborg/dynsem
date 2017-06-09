package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.CompilerDirectives.ValueType;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

@ValueType
public class RuleResult implements TruffleObject {
	public final Object result;
	public final Object[] components;

	public RuleResult(Object result, Object[] components) {
		this.result = result;
		this.components = components;
	}

	@Override
	public ForeignAccess getForeignAccess() {
		throw new UnsupportedOperationException();
//		return ForeignAccess.create(new DynSemRuleForeignAccess());
	}
}
