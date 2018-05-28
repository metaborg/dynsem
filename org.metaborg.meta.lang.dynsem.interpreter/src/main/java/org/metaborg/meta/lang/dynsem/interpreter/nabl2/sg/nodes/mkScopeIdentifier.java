package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "resource", type = TermBuild.class),
		@NodeChild(value = "name", type = TermBuild.class) })
public abstract class mkScopeIdentifier extends NativeOpBuild {

	public mkScopeIdentifier(SourceSection source) {
		super(source);
	}

	@Specialization
	public ScopeIdentifier executeSpecial(String resource, String name) {
		return new ScopeIdentifier(resource, name);
	}

}
