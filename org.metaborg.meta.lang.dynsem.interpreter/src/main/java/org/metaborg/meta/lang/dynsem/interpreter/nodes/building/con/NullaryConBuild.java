package org.metaborg.meta.lang.dynsem.interpreter.nodes.building.con;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NullaryConBuild extends TermBuild {

	private final String name;
	private final String sort;
	private final String dispatchKey;

	public NullaryConBuild(SourceSection source, String name, String sort) {
		super(source);
		this.sort = sort;
		this.name = name;
		this.dispatchKey = (name + "/0").intern();
	}

	@Specialization
	public ApplTerm doCached(VirtualFrame frame, @Cached("createAppl()") ApplTerm cached) {
		return cached;
	}

	protected ApplTerm createAppl() {
		return new ApplTerm(sort, name, new Object[0], dispatchKey, null);
	}

}
