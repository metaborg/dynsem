package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "list", type = TermBuild.class) })
public abstract class ListReverseTermBuild extends TermBuild {

	public ListReverseTermBuild(SourceSection source) {
		super(source);
	}

	@SuppressWarnings("rawtypes")
	@Specialization
	public IListTerm doEvaluated(IListTerm l) {
		return l.reverse();
	}

	public static ListReverseTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Reverse", 1);

		TermBuild list = TermBuild.create(Tools.applAt(t, 0), fd);

		return ListReverseTermBuildNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), list);
	}

}
