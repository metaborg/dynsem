package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "list", type = TermBuild.class) })
public abstract class ListLengthTermBuild extends TermBuild {

	public ListLengthTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	public int doNil(Nil l) {
		return 0;
	}

	@Specialization
	public int doCons(Cons cons) {
		return cons.size();
	}

	public static ListLengthTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "ListLength", 1);

		TermBuild list = TermBuild.create(Tools.applAt(t, 0), fd);

		return ListLengthTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), list);
	}

}
