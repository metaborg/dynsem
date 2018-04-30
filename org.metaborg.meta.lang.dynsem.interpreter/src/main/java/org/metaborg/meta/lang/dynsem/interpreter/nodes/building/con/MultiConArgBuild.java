package org.metaborg.meta.lang.dynsem.interpreter.nodes.building.con;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "rest", type = ConArgBuild.class) })
public abstract class MultiConArgBuild extends ConArgBuild {

	public MultiConArgBuild(SourceSection source) {
		super(source);
	}

	@Specialization(limit = "1", guards = { "arg == arg_cached", "rest == rest_cached" })
	public Cons doCached(Object arg, Cons rest, @Cached("arg") Object arg_cached, @Cached("rest") Cons rest_cached,
			@Cached("createCons(arg_cached, rest_cached)") Cons result_cached) {
		return result_cached;
	}

	@Specialization(replaces = "doCached")
	public Cons doUncached(Object arg, Cons rest) {
		return createCons(arg, rest);
	}

	protected static Cons createCons(Object arg, Cons rest) {
		return new Cons(null, arg, rest, null);
	}

}
