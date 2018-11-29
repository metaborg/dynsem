package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNodeGen.InvokeHelperNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DynamicDispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.ConstantTermDispatchNode;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.NeverValidAssumption;

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

	public abstract RuleResult executeGeneric(VirtualFrame frame, Object[] callArgs);

	@Specialization(guards = "termNode.isConstantNode()", assumptions = "constantTermAssumption")
	public RuleResult doConstantTerm(VirtualFrame frame, Object[] callArgs,
			@Cached("getConstantInputAssumption()") Assumption constantTermAssumption,
			@Cached("evalLhsTermNode(frame)") Object inputTerm,
			@Cached("create(arrowName, constantTermAssumption)") InvokeHelper helperNode) {
		return helperNode.executeGeneric(frame, callArgs, inputTerm);
	}

	@Specialization(replaces = "doConstantTerm")
	public RuleResult doDynamicTerm(VirtualFrame frame, Object[] callArgs,
			@Cached("create(arrowName)") InvokeHelper helperNode) {
		return helperNode.executeGeneric(frame, callArgs, evalLhsTermNode(frame));
	}

	public static abstract class InvokeHelper extends Node {
		protected final String arrowName;
		protected Assumption constantTermAssumption;

		public InvokeHelper(String arrowName, Assumption constantTermAssumption) {
			this.arrowName = arrowName;
			this.constantTermAssumption = constantTermAssumption;
		}

		public abstract RuleResult executeGeneric(VirtualFrame frame, Object[] callArgs, Object term);

		@Specialization(assumptions = "constantTermAssumption")
		public RuleResult doConstant(VirtualFrame frame, Object[] callArgs, Object term,
				@Cached("create(term.getClass(), arrowName)") ConstantTermDispatchNode dispatchNode) {
			callArgs[0] = term;
			return dispatchNode.execute(callArgs);
		}

		// @Specialization(replaces = "doConstant", guards = "term.getClass() == cachedDispatchClass", limit = "1")
		// public RuleResult doClass(VirtualFrame frame, Object[] callArgs, Object term,
		// @Cached("term.getClass()") Class<?> cachedDispatchClass,
		// @Cached("create(term.getClass(), arrowName)") ConstantClassDispatchNode dispatchNode) {
		// callArgs[0] = term;
		// return dispatchNode.execute(callArgs);
		// }

		@Specialization(replaces = "doConstant")
		public RuleResult doDynamic(VirtualFrame frame, Object[] callArgs, Object term,
				@Cached("create(getSourceSection(), arrowName)") DynamicDispatchNode dynamicDispatch) {
			// InterpreterUtils.printlnErr("Dynamic dispatch in RuleInvokeNode not implemented");
			// // TODO: this is probably required for function calls...
			// throw new ReductionFailure(
			// "Dynamic dispatch encountered, but support is not implemented. Reducing term: " + term,
			// InterpreterUtils.createStacktrace(), this);
			// throw new RuntimeException("Dynamic dispatch encountered, but support is not implemented");
			callArgs[0] = term;
			return dynamicDispatch.execute(callArgs);
		}

		public static InvokeHelper create(String arrowName, Assumption constantTermAssumption) {
			return InvokeHelperNodeGen.create(arrowName, constantTermAssumption);
		}

		public static InvokeHelper create(String arrowName) {
			return InvokeHelperNodeGen.create(arrowName, NeverValidAssumption.INSTANCE);
		}
	}

}
