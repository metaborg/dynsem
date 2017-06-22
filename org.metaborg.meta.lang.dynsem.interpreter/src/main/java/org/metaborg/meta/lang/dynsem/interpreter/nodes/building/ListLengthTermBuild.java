package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
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

	@SuppressWarnings("rawtypes")
	@Specialization
	public int doEvaluated(IListTerm l) {
		return l.size();
	}

	public static ListLengthTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "ListLength", 1);

		TermBuild list = TermBuild.create(Tools.applAt(t, 0), fd);

		return ListLengthTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), list);
	}

}
