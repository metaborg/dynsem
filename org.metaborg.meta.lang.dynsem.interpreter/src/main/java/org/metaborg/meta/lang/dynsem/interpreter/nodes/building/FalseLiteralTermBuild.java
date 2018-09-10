package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class FalseLiteralTermBuild extends TermBuild {

	public FalseLiteralTermBuild(SourceSection source) {
		super(source);
	}

	public static FalseLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "False", 0);
		return FalseLiteralTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Specialization
	public boolean executeFalse() {
		return false;
	}

}