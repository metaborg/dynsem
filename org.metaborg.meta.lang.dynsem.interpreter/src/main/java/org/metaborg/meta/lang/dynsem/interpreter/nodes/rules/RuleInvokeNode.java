package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.ConstantDispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
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
	@Children protected final TermBuild[] componentNodes;

	public RuleInvokeNode(SourceSection source, String arrowName, TermBuild termNode, TermBuild[] componentNodes) {
		super(source);
		this.arrowName = arrowName;
		this.termNode = termNode;
		this.componentNodes = componentNodes;
	}

	public abstract RuleResult execute(VirtualFrame frame);

	@TruffleBoundary
	protected final Object _logInlining(Object inputTerm) {
		System.out.println("Inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
				+ "-> (under root " + getRootNode() + ")");
		return null;
	}

	@TruffleBoundary
	protected final Object _logNotInlining(Object inputTerm) {
		System.out.println("Not inlining rules for: " + inputTerm.getClass().getSimpleName() + "-" + arrowName
				+ "-> (under root " + getRootNode() + ")");
		return null;
	}

	@Specialization(guards = "lhsIsConst", assumptions = "constantTermAssumption")
	@ExplodeLoop
	public RuleResult doInlinedDispatch(VirtualFrame frame, @Cached("termNode.isConstantNode()") boolean lhsIsConst,
			@Cached("evalLhsTermNode(frame)") Object inputTerm,
			@Cached("getConstantInputAssumption()") Assumption constantTermAssumption,
			@Cached("create(inputTerm, arrowName)") ConstantDispatchNode dispatchNode
	// , @Cached("_logInlining(inputTerm)") Object __dc
	) {

		Object[] args = new Object[componentNodes.length + 1];
		args[0] = inputTerm;

		CompilerAsserts.compilationConstant(componentNodes.length);
		for (int i = 0; i < componentNodes.length; i++) {
			InterpreterUtils.setComponent(getContext(), args, i + 1, componentNodes[i].executeGeneric(frame), this);
		}
		return dispatchNode.execute(args);
	}

	@CompilationFinal private boolean loggedNotInlined;

	@Specialization(replaces = "doInlinedDispatch")
	@ExplodeLoop
	public RuleResult doDynamicDispatch(VirtualFrame frame,
			@Cached("create(getSourceSection(), arrowName)") DispatchNode dispatchNode) {
		Object[] args = new Object[componentNodes.length + 1];
		Object term = evalLhsTermNode(frame);
		// if (!loggedNotInlined) {
		// CompilerAsserts.neverPartOfCompilation();
		// loggedNotInlined = true;
		// _logNotInlining(term);
		// }

		args[0] = term;

		CompilerAsserts.compilationConstant(componentNodes.length);
		for (int i = 0; i < componentNodes.length; i++) {
			InterpreterUtils.setComponent(getContext(), args, i + 1, componentNodes[i].executeGeneric(frame), this);
		}
		return dispatchNode.execute(term.getClass(), args);
	}

	protected final Object evalLhsTermNode(VirtualFrame frame) {
		return termNode.executeGeneric(frame);
	}
}
