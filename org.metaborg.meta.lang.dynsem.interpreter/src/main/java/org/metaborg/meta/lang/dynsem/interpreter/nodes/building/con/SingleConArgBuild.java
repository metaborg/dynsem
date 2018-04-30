package org.metaborg.meta.lang.dynsem.interpreter.nodes.building.con;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class SingleConArgBuild extends ConArgBuild {

	public SingleConArgBuild(SourceSection source) {
		super(source);
	}

	@Specialization(limit = "1", guards = { "arg == arg_cached" })
	public Cons doCached(Object arg, @Cached("arg") Object arg_cached,
			@Cached("createCons(arg_cached)") Cons result_cached) {
		return result_cached;
	}

	@Specialization(replaces = "doCached")
	public Cons doUncached(Object arg) {
		return createCons(arg);
	}

	protected static Cons createCons(Object arg) {
		return new Cons(null, arg, new Nil(null, null), null);
	}

}
