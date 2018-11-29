package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.source.SourceSection;

public class BoundaryRuleNode extends RuleNode {
	private final Assumption constantTermAssumption;

	public BoundaryRuleNode(SourceSection source, RuleInputsNode inputsNode, Premise[] premises, RuleTarget output) {
		super(source, inputsNode, premises, output);
		this.constantTermAssumption = Truffle.getRuntime().createAssumption("constant input boundary assumption");
	}

	@Override
	public Assumption getConstantInputAssumption() {
		return this.constantTermAssumption;
	}

	@TruffleBoundary
	public final static BoundaryRuleNode createFromRuleNode(RuleNode r) {
		return new BoundaryRuleNode(r.getSourceSection(), r.inputsNode, r.premises, r.target);
	}

}
