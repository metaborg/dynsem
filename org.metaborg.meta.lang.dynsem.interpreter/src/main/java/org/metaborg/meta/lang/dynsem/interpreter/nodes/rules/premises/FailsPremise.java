package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
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

	public static FailsPremise create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Fails", 1);
		return new FailsPremise(SourceUtils.dynsemSourceSectionFromATerm(t),
				Premise.create(lang, Tools.termAt(t, 0), fd));
	}
}
