package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.calls.DynamicDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.InlinedDispatch;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
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

	public abstract RuleResult executeGeneric(VirtualFrame frame, Object[] callArgs);

	@Specialization(assumptions = "constantBuildAssumption")
	public RuleResult doConstantTerm(VirtualFrame frame, Object[] callArgs,
			@Cached("termNode.getConstantBuildAssumption()") Assumption constantBuildAssumption,
			@Cached("evalLhsTermNode(frame)") Object inputTerm,
			@Cached("create(getSourceSection(), arrowName)") InlinedDispatch inlinedDispatch) {
		// TODO: it may be beneficial to merge InlinedDispatch into this node
		callArgs[0] = inputTerm;
		return inlinedDispatch.execute(callArgs);
	}

	@Specialization(replaces = "doConstantTerm")
	public RuleResult doDynamicTerm(VirtualFrame frame, Object[] callArgs,
			@Cached("create(getSourceSection(), arrowName)") DynamicDispatch dynamicDispatch) {
		// TODO: it may be beneficial to merge DynamicDispatch into this node
		Object inputTerm = evalLhsTermNode(frame);
		callArgs[0] = inputTerm;
		return dynamicDispatch.execute(callArgs);
	}

}
