package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNodeGen.InvokeHelperNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.calls.DynamicDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.InlinedDispatch;

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
		return helperNode.executeGeneric(callArgs, inputTerm);
	}

	@Specialization(replaces = "doConstantTerm")
	public RuleResult doDynamicTerm(VirtualFrame frame, Object[] callArgs,
			@Cached("create(arrowName)") InvokeHelper helperNode) {
		Object inputTerm = evalLhsTermNode(frame);
		return helperNode.executeGeneric(callArgs, inputTerm);
	}

	public static abstract class InvokeHelper extends Node {
		protected final String arrowName;
		protected Assumption constantTermAssumption;

		public InvokeHelper(String arrowName, Assumption constantTermAssumption) {
			this.arrowName = arrowName;
			this.constantTermAssumption = constantTermAssumption;
		}

		public abstract RuleResult executeGeneric(Object[] callArgs, Object term);

		@Specialization(assumptions = "constantTermAssumption")
		public RuleResult doConstant(Object[] callArgs, Object term,
				@Cached("create(getSourceSection(), arrowName)") InlinedDispatch dispatchNode) {
			callArgs[0] = term;
			return dispatchNode.execute(callArgs);
		}

		@Specialization(replaces = "doConstant")
		public RuleResult doDynamic(Object[] callArgs, Object term,
				@Cached("create(getSourceSection(), arrowName)") DynamicDispatch dynamicDispatch) {
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
