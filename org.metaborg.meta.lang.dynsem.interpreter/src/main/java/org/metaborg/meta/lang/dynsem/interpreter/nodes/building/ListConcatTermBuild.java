package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class ListConcatTermBuild extends TermBuild {

	public ListConcatTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "l == l_cached", "r == r_cached" })
	@SuppressWarnings({ "rawtypes" })
	public IListTerm doCachedBoth(IListTerm l, IListTerm r, @Cached("l") IListTerm l_cached,
			@Cached("r") IListTerm r_cached, @Cached("doUncached(l_cached, r_cached)") IListTerm result) {
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Specialization(replaces="doCachedBoth", guards = { "l == l_cached" })
	@ExplodeLoop
	public IListTerm doCachedLeft(IListTerm l, IListTerm r, @Cached("l") IListTerm l_cached,
			@Cached(value = "toArray(l_cached)", dimensions = 1) Object[] l_elems) {
		IListTerm result = r;
		for(int i = l_elems.length - 1; i >= 0; i--) {
			result = result.prefix(l_elems[i]);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	protected static Object[] toArray(IListTerm l) {
		return l.toArray();
	}

	@Specialization(replaces = { "doCachedBoth", "doCachedLeft" })
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IListTerm doUncached(IListTerm l, IListTerm r) {
		return r.prefixAll(l);
	}

}
