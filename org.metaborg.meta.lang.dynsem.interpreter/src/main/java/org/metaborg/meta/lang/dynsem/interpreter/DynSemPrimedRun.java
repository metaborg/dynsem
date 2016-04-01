package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class DynSemPrimedRun implements TruffleObject {

	private RootCallTarget callTarget;
	private ITerm program;

	public void setCallTarget(RootCallTarget callTarget) {
		this.callTarget = callTarget;
	}

	public RootCallTarget getCallTarget() {
		return callTarget;
	}

	public void setProgram(ITerm program) {
		this.program = program;
	}

	public ITerm getProgram() {
		return program;
	}

	@Override
	public ForeignAccess getForeignAccess() {
		return DynSemRunForeignAccess.INSTANCE;
	}

}
