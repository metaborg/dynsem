package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class RecursiveReductionRule extends ReductionRule {

	public RecursiveReductionRule(SourceSection source, FrameDescriptor fd, RuleKind kind, String arrowName,
			Class<?> dispatchClass, RuleInputsNode inputsNode, Premise[] premises, RuleTarget output) {
		super(source, fd, kind, arrowName, dispatchClass, inputsNode, premises, output);
	}

	private final BranchProfile recurTaken = BranchProfile.create();

	@Override
	public RuleResult execute(VirtualFrame frame) {
		RuleResult result = null;
		boolean repeat = true;
		while (repeat) {
			try {
				result = super.execute(frame);
				repeat = false;
			} catch (RecurException recex) {
				recurTaken.enter();
				repeat = true;
			}
		}

		assert result != null;
		return result;
	}

}
