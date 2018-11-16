package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@Deprecated
public class FailsPremise extends Premise {

	@Child protected Premise premise;

	public FailsPremise(SourceSection source, Premise premise) {
		super(source);
		this.premise = premise;
	}

	@Override
	public void execute(VirtualFrame frame) {

		boolean premiseSucceeds = true;
		try {
			premise.execute(frame);
		} catch (PremiseFailureException pf) {
			premiseSucceeds = false;
		}

		if (premiseSucceeds) {
			throw PremiseFailureException.SINGLETON;
		}

	}

}
