package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ConsNilList;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "head", type = TermBuild.class),
		@NodeChild(value = "tail", type = TermBuild.class) })
public abstract class ConsBuild extends ListBuild {

	protected final String listSort;

	public ConsBuild(SourceSection source, String listSort) {
		super(source);
		this.listSort = listSort;
	}

	@Specialization(guards = "head == head_cached")
	public Cons doBuildOnNil(VirtualFrame frame, Object head, Nil tail, @Cached("head") Object head_cached,
			@Cached("createCons(head_cached, tail)") Cons cons_cached) {
		return cons_cached;
	}

	@Specialization(guards = { "head == head_cached", "tail == tail_cached" })
	public Cons doBuildOnCons(VirtualFrame frame, Object head, Cons tail, @Cached("head") Object head_cached,
			@Cached("tail") Cons tail_cached, @Cached("createCons(head_cached, tail)") Cons cons_cached) {
		return cons_cached;
	}

	@Specialization(replaces = { "doBuildOnNil", "doBuildOnCons" })
	public Cons doUncached(VirtualFrame frame, Object head, ConsNilList tail) {
		return createCons(head, tail);
	}

	protected Cons createCons(Object head, ConsNilList tail) {
		return new Cons(listSort, head, tail, tail.getStrategoTerm());
	}


}
