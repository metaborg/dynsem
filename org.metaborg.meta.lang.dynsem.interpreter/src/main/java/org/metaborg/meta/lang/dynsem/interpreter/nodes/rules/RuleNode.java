package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public final class RuleNode extends DynSemNode {

	public final static String DEFAULT_NAME = "";

	@Child protected RuleInputsNode inputsNode;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public RuleNode(SourceSection source, RuleInputsNode inputsNode, Premise[] premises, RuleTarget output) {
		super(source);

		this.inputsNode = inputsNode;
		this.premises = premises;
		this.target = output;
	}

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
