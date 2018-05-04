package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Cached;
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

	@SuppressWarnings({ "rawtypes" })
	@Specialization(guards = { "l == l_cached", "r == r_cached" })
	public IListTerm doCachedRight(IListTerm l, IListTerm r, @Cached("l") IListTerm l_cached,
			@Cached("r") IListTerm r_cached, @Cached("doUncached(l_cached, r_cached)") IListTerm result_cached) {
		return result_cached;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Specialization(guards = "l == l_cached")
	public IListTerm doCachedLeft(IListTerm l, IListTerm r, @Cached("l") IListTerm l_cached,
			@Cached(value = "toArray(l_cached)", dimensions = 1) Object[] l_elems) {
		return r.addAll(l_elems);
	}

	@SuppressWarnings("rawtypes")
	protected static Object[] toArray(IListTerm l) {
		return l.toArray();
	}

	@Specialization
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IListTerm doUncached(IListTerm l, IListTerm r) {
		return r.addAll(toArray(l));
	}

	public static ListConcatTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "ListConcat", 2);

		TermBuild left = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild right = TermBuild.create(Tools.applAt(t, 1), fd);

		return ListConcatTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

}
