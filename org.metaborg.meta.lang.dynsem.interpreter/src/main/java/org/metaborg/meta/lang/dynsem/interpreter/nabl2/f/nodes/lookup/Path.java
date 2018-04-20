package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class Path extends RootNode {

	@Child private PathStep p;

	public Path(DynSemLanguage lang, PathStep p) {
		super(lang);
		this.p = p;
		Truffle.getRuntime().createCallTarget(this);
	}

	public Occurrence getTargetDec() {
		return p.getTargetDec();
	}

	@Override
	public FrameAddr execute(VirtualFrame frame) {
		return p.executeLookup(FrameLayoutUtil.layout().getType().cast(frame.getArguments()[0]));
	}

	public static Path create(IStrategoList steps, DynSemLanguage lang) {
		return new Path(lang, createSteps(steps));
	}

	public static PathStep createSteps(IStrategoList steps) {
		PathStep tailStep = null;
		for (int i = steps.size() - 1; i >= 0; i--) {
			IStrategoAppl stepTerm = Tools.applAt(steps, i);
			if (Tools.hasConstructor(stepTerm, "D")) {
				assert tailStep == null;
				tailStep = D.create(stepTerm);
				continue;
			}
			if (Tools.hasConstructor(stepTerm, "E")) {
				assert tailStep != null;
				tailStep = E.create(stepTerm, tailStep);
				continue;
			}
			if (Tools.hasConstructor(stepTerm, "N")) {
				assert tailStep == null;
				tailStep = N.create(stepTerm);
				continue;
			}
			throw new IllegalStateException("Unsupported path step: " + stepTerm);
		}
		assert tailStep != null;
		return tailStep;
	}

}
