package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemRunForeignAccess;

import com.oracle.truffle.api.CompilerDirectives.ValueType;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

@ValueType
public class RuleResult implements TruffleObject {
	public Object result;
	public Object[] components;

	@Override
	public ForeignAccess getForeignAccess() {
		return DynSemRunForeignAccess.INSTANCE;
	}
}
