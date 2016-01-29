package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * A node corresponding to a (merged) DynSem rule.
 * 
 * 
 * @author vladvergu
 *
 */
public class Rule extends RootNode {

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public Rule(Premise[] premises, RuleTarget output, SourceSection source,
			FrameDescriptor fd) {
		super(DynSemLanguage.class, source, fd);
		this.premises = premises;
		this.target = output;
		Truffle.getRuntime().createCallTarget(this);
	}

	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		/* evaluate the premises */
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}

		/* evaluate the rule target */
		return target.execute(frame);
	}

}
