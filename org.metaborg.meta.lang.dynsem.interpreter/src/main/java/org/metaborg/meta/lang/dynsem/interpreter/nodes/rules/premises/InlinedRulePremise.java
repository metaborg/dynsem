package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.ConReductionPremiseLHS;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class InlinedRulePremise extends Premise {

	@Child protected Rule rule;
	@Child protected ConReductionPremiseLHS lhs;
	private final FrameDescriptor fd;

	public InlinedRulePremise(Rule rule, FrameDescriptor fd,
			SourceSection sourceSection) {
		super(sourceSection);
		this.rule = rule;
		this.fd = fd;
	}

	public void execute(VirtualFrame frame) {
		Object[] args = lhs.executeObjectArray(frame);
		rule.execute(Truffle.getRuntime().createVirtualFrame(args, fd));
	}
}
