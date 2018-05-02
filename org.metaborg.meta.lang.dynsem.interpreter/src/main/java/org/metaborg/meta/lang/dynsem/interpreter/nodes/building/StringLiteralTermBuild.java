package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class StringLiteralTermBuild extends TermBuild {

	private final String val;

	public StringLiteralTermBuild(String val, SourceSection source) {
		super(source);
		this.val = val.intern();
	}

	public static StringLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		return StringLiteralTermBuildNodeGen.create(Tools.javaStringAt(t, 0),
				SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Specialization
	public String executeCreate() {
		return val;
	}
}