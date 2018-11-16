package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "list", type = TermBuild.class) })
public abstract class ListLengthTermBuild extends TermBuild {

	public ListLengthTermBuild(SourceSection source) {
		super(source);
	}

	@SuppressWarnings("rawtypes")
	@Specialization
	public int doEvaluated(IListTerm l) {
		return l.size();
	}

}
