package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynamicRuleInvokeNode extends DynSemNode {

	protected final String arrowName;

	@Child protected TermBuild termNode;
	@Children protected final TermBuild[] componentNodes;

	public DynamicRuleInvokeNode(SourceSection source, String arrowName, TermBuild termNode, TermBuild[] componentNodes) {
		super(source);
		this.arrowName = arrowName;
		this.termNode = termNode;
		this.componentNodes = componentNodes;
	}

	public abstract RuleResult execute(VirtualFrame frame);

	@Specialization
	@ExplodeLoop
	public RuleResult doDynamicDispatch(VirtualFrame frame,
			@Cached("create(getSourceSection(), arrowName)") DispatchNode dispatchNode) {
		// FIXME: if getConstantAssumption() is valid && the input term to the reduction is constant (i.e. is a
		// ConstRead) then we should inline the
		// intended rule
		/*
		 * NB1: the rule to be inlined will invalidate OUR assumption if it sees varying terms, so we should be careful
		 * to inline only those rules which will observe a constant input term. This is guaranteed by us having a
		 * ConstRead in the premise and a valid assumption
		 */
		Object[] args = new Object[componentNodes.length + 1];
		Object term = termNode.executeGeneric(frame);
		args[0] = term;

		CompilerAsserts.compilationConstant(componentNodes.length);
		for (int i = 0; i < componentNodes.length; i++) {
			InterpreterUtils.setComponent(getContext(), args, i + 1, componentNodes[i].executeGeneric(frame), this);
		}
		return dispatchNode.execute(term.getClass(), args);
	}
}
