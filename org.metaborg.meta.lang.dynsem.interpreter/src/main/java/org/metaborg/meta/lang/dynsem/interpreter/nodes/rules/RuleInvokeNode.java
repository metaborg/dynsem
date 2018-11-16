package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.ConstantTermDispatchNode;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RuleInvokeNode extends DynSemNode {

	protected final String arrowName;

	@Child protected TermBuild termNode;

	public RuleInvokeNode(SourceSection source, String arrowName, TermBuild termNode) {
		super(source);
		this.arrowName = arrowName;
		this.termNode = termNode;
	}

	protected final Object evalLhsTermNode(VirtualFrame frame) {
		return termNode.executeGeneric(frame);
	}

	public abstract RuleResult execute(VirtualFrame frame, Object[] callArgs);

	private final boolean logInlining = false;

	@Specialization(guards = "lhsIsConst", assumptions = "constantTermAssumption")
	@ExplodeLoop
	public RuleResult doConstantTermDispatch(VirtualFrame frame, Object[] callArgs,
			@Cached("termNode.isConstantNode()") boolean lhsIsConst, @Cached("evalLhsTermNode(frame)") Object inputTerm,
			@Cached("getConstantInputAssumption()") Assumption constantTermAssumption,
			@Cached("create(inputTerm.getClass(), arrowName)") ConstantTermDispatchNode dispatchNode) {
		_logInlining(inputTerm);
		callArgs[0] = inputTerm;

		return dispatchNode.execute(callArgs);
	}

	@Specialization(replaces = "doConstantTermDispatch")
	@ExplodeLoop
	public RuleResult doOpportunisticDispatch(VirtualFrame frame, Object[] callArgs,
			@Cached("create(getSourceSection(), arrowName)") DispatchNode dispatchHelperNode) {
		Object term = evalLhsTermNode(frame);
		_logNotInlining(term);
		callArgs[0] = term;

		return dispatchHelperNode.execute(callArgs);
	}

	// public static abstract class InvokeHelper extends Node {
	//
	// protected final String arrowName;
	//
	// public InvokeHelper(String arrowName) {
	// this.arrowName = arrowName;
	// }
	//
	// public abstract RuleResult execute(Object[] args, Object inputTerm);
	//
	// @Specialization(guards = "inputTerm.getClass() == cachedClass", limit = "1")
	// public RuleResult doConstantClassDispatch(Object[] args, Object inputTerm,
	// @Cached("inputTerm.getClass()") Class<?> cachedClass,
	// @Cached("create(inputTerm.getClass(), arrowName)") ConstantClassDispatchNode dispatchNode) {
	// _logInlining(inputTerm);
	// return dispatchNode.execute(args);
	// }
	//
	// @Specialization(replaces = "doConstantClassDispatch")
	// public RuleResult doDynamicDispatch(Object[] args, Object inputTerm,
	// @Cached("create(getSourceSection(), arrowName)") DispatchNode dispatchNode) {
	// _logNotInlining(inputTerm);
	// return dispatchNode.execute(args);
	// }
	//
	// private final boolean logInlining = true;
	// @CompilationFinal private boolean loggedNotInlined;
	//
	// private final void _logNotInlining(Object inputTerm) {
	// if (logInlining && !loggedNotInlined) {
	// loggedNotInlined = true;
	// __logNotInlining(inputTerm);
	// }
	// }
	//
	// @TruffleBoundary
	// private final void __logNotInlining(Object inputTerm) {
	// System.out.println("Not class-inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
	// + "-> (under root " + getRootNode() + ")");
	// }
	//
	// @CompilationFinal private boolean loggedInlined;
	//
	// private final void _logInlining(Object inputTerm) {
	// if (logInlining && !loggedInlined) {
	// loggedInlined = true;
	// __logInlining(inputTerm);
	// }
	// }
	//
	// @TruffleBoundary
	// private final void __logInlining(Object inputTerm) {
	// System.out.println("Class-Inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
	// + "-> (under root " + getRootNode() + ")");
	// }
	//
	// public static InvokeHelper create(String arrowName) {
	// return InvokeHelperNodeGen.create(arrowName);
	// }
	//
	// }

	@CompilationFinal private boolean loggedNotInlined;

	private final void _logNotInlining(Object inputTerm) {
		if (logInlining && !loggedNotInlined) {
			loggedNotInlined = true;
			__logNotInlining(inputTerm);
		}
	}

	@TruffleBoundary
	private final void __logNotInlining(Object inputTerm) {
		System.out.println("Not inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
				+ "-> (under root " + getRootNode() + ")");
	}

	@CompilationFinal private boolean loggedInlined;

	private final void _logInlining(Object inputTerm) {
		if (logInlining && !loggedInlined) {
			loggedInlined = true;
			__logInlining(inputTerm);
		}
	}

	@TruffleBoundary
	private final void __logInlining(Object inputTerm) {
		System.out.println("Inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
				+ "-> (under root " + getRootNode() + ")");
	}

}
