package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class TermBuildUnstableException extends ControlFlowException {

	public static TermBuildUnstableException INSTANCE = new TermBuildUnstableException();

	private TermBuildUnstableException() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2343395318177213492L;

}
