package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.TermIndex;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "namespace", type = TermBuild.class),
		@NodeChild(value = "name", type = TermBuild.class), @NodeChild(value = "termindex", type = TermBuild.class) })
public abstract class MakeOccurrence extends NativeOpBuild {

	public MakeOccurrence(SourceSection source) {
		super(source);
	}

	@Specialization
	public Occurrence execBuild(String namespace, String name, TermIndex index) {
		return new Occurrence(namespace, name, index);
	}

	public static MakeOccurrence create(SourceSection source, TermBuild namespace, TermBuild name,
			TermBuild termindex) {
		return ScopeNodeFactories.createMakeOccurrence(source, namespace, name, termindex);
	}
}
