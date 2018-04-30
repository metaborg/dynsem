package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NilBuild extends ListBuild {

	protected final String sort;

	public NilBuild(SourceSection source, String listSort) {
		super(source);
		this.sort = listSort;
	}

	@Specialization
	public Nil doCachedNil(@Cached("createNil(sort)") Nil nil) {
		return nil;
	}

	protected static Nil createNil(String listSort) {
		return new Nil(listSort, null);
	}

}
