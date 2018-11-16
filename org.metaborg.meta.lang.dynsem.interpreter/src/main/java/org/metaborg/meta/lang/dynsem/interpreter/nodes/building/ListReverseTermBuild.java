package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "list", type = TermBuild.class) })
public abstract class ListReverseTermBuild extends TermBuild {

	public ListReverseTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization(guards = "l == l_cached")
	@SuppressWarnings("rawtypes")
	public IListTerm doCached(IListTerm l, @Cached("l") IListTerm l_cached,
			@Cached("doUncached(l_cached)") IListTerm l_reversed_cached) {
		return l_reversed_cached;
	}

	@Specialization(replaces = "doCached")
	@SuppressWarnings("rawtypes")
	public IListTerm doUncached(IListTerm l) {
		return l.reverse();
	}

}
