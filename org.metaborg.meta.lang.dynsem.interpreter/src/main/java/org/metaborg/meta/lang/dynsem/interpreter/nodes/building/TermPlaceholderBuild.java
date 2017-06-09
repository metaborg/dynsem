package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.TermPlaceholder;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class TermPlaceholderBuild extends TermBuild {

	public TermPlaceholderBuild(SourceSection source) {
		super(source);
	}

	public static TermPlaceholderBuild create(IStrategoAppl t, FrameDescriptor fd) {
		return new TermPlaceholderBuild(DynSemLanguage.getSourceSectionFromStrategoTerm(t));
	}

	@Override
	public ITerm executeGeneric(VirtualFrame frame) {
		return TermPlaceholder.INSTANCE;
	}

}
