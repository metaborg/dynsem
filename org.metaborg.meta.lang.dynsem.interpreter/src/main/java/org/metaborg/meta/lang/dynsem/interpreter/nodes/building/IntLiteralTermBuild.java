package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IntLiteralTermBuild extends TermBuild {

	private final int val;

	public IntLiteralTermBuild(SourceSection source, int val) {
		super(source);
		this.val = val;
	}

	public static IntLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		return IntLiteralTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				Integer.parseInt(Tools.javaStringAt(t, 0)));
	}

	@Specialization
	public int executeInteger() {
		return val;
	}

}