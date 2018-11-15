package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNodeGen.InvokeHelperNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.ConstantClassDispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.ConstantTermDispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RuleInvokeNode extends DynSemNode {

	protected final String arrowName;

	@Child protected TermBuild termNode;
	@Children protected final TermBuild[] componentNodes;

	public RuleInvokeNode(SourceSection source, String arrowName, TermBuild termNode, TermBuild[] componentNodes) {
		super(source);
		this.arrowName = arrowName;
		this.termNode = termNode;
		this.componentNodes = componentNodes;
	}

	protected final Object evalLhsTermNode(VirtualFrame frame) {
		return termNode.executeGeneric(frame);
	}

	public abstract RuleResult execute(VirtualFrame frame);

	private final boolean logInlining = true;

	@Specialization(guards = "lhsIsConst", assumptions = "constantTermAssumption")
	@ExplodeLoop
	public RuleResult doConstantTermDispatch(VirtualFrame frame,
			@Cached("termNode.isConstantNode()") boolean lhsIsConst, @Cached("evalLhsTermNode(frame)") Object inputTerm,
			@Cached("getConstantInputAssumption()") Assumption constantTermAssumption,
			@Cached("create(inputTerm.getClass(), arrowName)") ConstantTermDispatchNode dispatchNode) {
		_logInlining(inputTerm);
		Object[] args = new Object[componentNodes.length + 1];
		args[0] = inputTerm;

		CompilerAsserts.compilationConstant(componentNodes.length);
		for (int i = 0; i < componentNodes.length; i++) {
			InterpreterUtils.setComponent(getContext(), args, i + 1, componentNodes[i].executeGeneric(frame), this);
		}
		return dispatchNode.execute(args);
	}

	@Specialization(replaces = "doConstantTermDispatch")
	@ExplodeLoop
	public RuleResult doOpportunisticDispatch(VirtualFrame frame,
			@Cached("create(arrowName)") InvokeHelper dispatchHelperNode) {
		Object[] args = new Object[componentNodes.length + 1];
		Object term = evalLhsTermNode(frame);
		_logNotInlining(term);

		args[0] = term;
		CompilerAsserts.compilationConstant(componentNodes.length);
		for (int i = 0; i < componentNodes.length; i++) {
			InterpreterUtils.setComponent(getContext(), args, i + 1, componentNodes[i].executeGeneric(frame), this);
		}
		return dispatchHelperNode.execute(args, term);
	}

	public static abstract class InvokeHelper extends Node {

		protected final String arrowName;

		public InvokeHelper(String arrowName) {
			this.arrowName = arrowName;
		}

		public abstract RuleResult execute(Object[] args, Object inputTerm);

		@Specialization(guards = "inputTerm.getClass() == cachedClass", limit = "1")
		public RuleResult doConstantClassDispatch(Object[] args, Object inputTerm,
				@Cached("inputTerm.getClass()") Class<?> cachedClass,
				@Cached("create(inputTerm.getClass(), arrowName)") ConstantClassDispatchNode dispatchNode) {
			_logInlining(inputTerm);
			return dispatchNode.execute(args);
		}

		@Specialization(replaces = "doConstantClassDispatch")
		public RuleResult doDynamicDispatch(Object[] args, Object inputTerm,
				@Cached("create(getSourceSection(), arrowName)") DispatchNode dispatchNode) {
			_logNotInlining(inputTerm);
			return dispatchNode.execute(args);
		}

		private final boolean logInlining = true;
		@CompilationFinal private boolean loggedNotInlined;

		private final void _logNotInlining(Object inputTerm) {
			if (logInlining && !loggedNotInlined) {
				loggedNotInlined = true;
				__logNotInlining(inputTerm);
			}
		}

		@TruffleBoundary
		private final void __logNotInlining(Object inputTerm) {
			System.out.println("Not class-inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
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
			System.out.println("Class-Inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
					+ "-> (under root " + getRootNode() + ")");
		}

		public static InvokeHelper create(String arrowName) {
			return InvokeHelperNodeGen.create(arrowName);
		}

	}

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
