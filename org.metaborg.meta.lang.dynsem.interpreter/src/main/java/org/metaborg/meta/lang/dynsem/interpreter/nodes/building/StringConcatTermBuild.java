package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class StringConcatTermBuild extends TermBuild {

	public StringConcatTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	@TruffleBoundary
	public String executeStrings(String l, String r) {
		return l + r;
	}

	public static StringConcatTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "StrConcat", 2);

		TermBuild left = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild right = TermBuild.create(Tools.applAt(t, 1), fd);

		return StringConcatTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

}
