package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Fresh extends NativeOpBuild {

	private static int next = 0;

	public Fresh(SourceSection source) {
		super(source);
	}

	public static Fresh create(IStrategoAppl t, FrameDescriptor fd) {
		return FreshNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Specialization
	public int execIncrement() {
		return next++;
	}

}
