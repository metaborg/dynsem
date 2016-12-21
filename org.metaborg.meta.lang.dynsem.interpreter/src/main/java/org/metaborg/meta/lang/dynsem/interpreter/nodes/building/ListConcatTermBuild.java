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

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class ListConcatTermBuild extends TermBuild {

	public ListConcatTermBuild(SourceSection source) {
		super(source);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Specialization
	public IListTerm doLists(IListTerm l, IListTerm r){
		return l.addAll(r.toArray());
	}
	
	
	public static ListConcatTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "ListConcat", 2);

		TermBuild left = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild right = TermBuild.create(Tools.applAt(t, 1), fd);

		return ListConcatTermBuildNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), left, right);
	}

}
