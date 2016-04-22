package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class Fresh extends TermBuild {

	private static int next = Integer.MIN_VALUE;

	public Fresh(SourceSection source) {
		super(source);
	}

	public static Fresh create(IStrategoAppl t, FrameDescriptor fd) {
		return new Fresh(SourceSectionUtil.fromStrategoTerm(t));
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
