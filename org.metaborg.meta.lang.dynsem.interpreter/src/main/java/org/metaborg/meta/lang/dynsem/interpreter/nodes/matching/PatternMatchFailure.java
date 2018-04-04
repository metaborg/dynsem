package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * {@link ControlFlowException} to signal that a pattern match application has failed.
 * 
 * @author vladvergu
 *
 */
public class PatternMatchFailure extends ControlFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4485713452349320066L;

	public static final PatternMatchFailure INSTANCE = new PatternMatchFailure();

	private PatternMatchFailure() {
	}

	// @Override
	// public String getMessage() {
	// return "Pattern match failure: \n" + trace;
	// }
	//
	//
	// public static PatternMatchFailure create(DynSemContext ctx) {
	// CompilerAsserts.compilationConstant(ctx.isDEBUG());
	// if (ctx.isDEBUG()) {
	// return new PatternMatchFailure(InterpreterUtils.createStacktrace());
	// } else {
	// return INSTANCE;
	// }
	// }

}
