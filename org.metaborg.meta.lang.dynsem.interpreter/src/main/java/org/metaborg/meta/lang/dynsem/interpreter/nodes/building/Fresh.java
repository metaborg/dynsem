package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class Fresh extends TermBuild {

	private static int next = 0;

	public Fresh(SourceSection source) {
		super(source);
	}

	public static Fresh create(IStrategoAppl t, FrameDescriptor fd) {
		return new Fresh(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Override
	public Integer executeGeneric(VirtualFrame frame) {
		return executeInteger(frame);
	}

	@Override
	public int executeInteger(VirtualFrame frame) {
		return next++;
	}

}
