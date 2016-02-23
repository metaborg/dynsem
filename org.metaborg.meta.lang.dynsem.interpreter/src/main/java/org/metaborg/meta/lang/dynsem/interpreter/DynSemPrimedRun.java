package org.metaborg.meta.lang.dynsem.interpreter;

import metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class DynSemPrimedRun implements TruffleObject {

	private RootCallTarget callTarget;
	private IConTerm program;

	public void setCallTarget(RootCallTarget callTarget) {
		this.callTarget = callTarget;
	}

	public RootCallTarget getCallTarget() {
		return callTarget;
	}

	public void setProgram(IConTerm program) {
		this.program = program;
	}

	public IConTerm getProgram() {
		return program;
	}

	@Override
	public ForeignAccess getForeignAccess() {
		return DynSemRunForeignAccess.INSTANCE;
	}

}
