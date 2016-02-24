package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class PremisesSequence extends Premise {

	@Children protected final Premise[] premises;

	public PremisesSequence(Premise[] premises, SourceSection source) {
		super(source);
		this.premises = premises;
	}

	@Override
	@ExplodeLoop
	public void execute(VirtualFrame frame) {
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}
	}

}
