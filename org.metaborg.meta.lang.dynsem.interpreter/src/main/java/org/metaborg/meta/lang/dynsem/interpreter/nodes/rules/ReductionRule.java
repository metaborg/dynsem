package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * A node corresponding to a (merged) DynSem rule.
 * 
 *
 * A rule has inputs of three types: (1) Read-only semantic components (ROs), (2) Pattern-bound variables (PVs), (3)
 * Read-write semantic components (RWs)
 * 
 * These inputs are passed through the arguments array in the following order <ROs,PVs,RWs>. This order coincides with
 * the binding order inside the rule.
 * 
 * @author vladvergu
 *
 */
public class ReductionRule extends Rule {

	@Child protected RuleInputsNode inputsNode;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public ReductionRule(SourceSection source, FrameDescriptor fd, RuleKind kind, String arrowName,
			Class<?> dispatchClass, RuleInputsNode inputsNode, Premise[] premises, RuleTarget output) {
		super(source, fd, kind, arrowName, dispatchClass);
		this.inputsNode = inputsNode;
		this.premises = premises;
		this.target = output;
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		/* evaluate the inputs node */
		inputsNode.execute(frame);

		/* evaluate the premises */
		evaluatePremises(frame);

		/* evaluate the rule target */
		return target.execute(frame);
	}

	@ExplodeLoop
	private void evaluatePremises(VirtualFrame frame) {
		CompilerAsserts.compilationConstant(premises.length);
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}
	}

}
