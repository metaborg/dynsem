package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.TermPlaceholder;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TermPlaceholderBuild extends TermBuild {

	public TermPlaceholderBuild(SourceSection source) {
		super(source);
	}

	public static TermPlaceholderBuild create(IStrategoAppl t, FrameDescriptor fd) {
		return TermPlaceholderBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Specialization
	public ITerm executePlaceholder(VirtualFrame frame) {
		return TermPlaceholder.INSTANCE;
	}

}
