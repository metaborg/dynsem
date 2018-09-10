package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "s1", type = TermBuild.class), @NodeChild(value = "s2", type = TermBuild.class) })
public abstract class ScopesEqual extends NativeOpBuild {

	public ScopesEqual(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean eqCheck(ScopeIdentifier s1, ScopeIdentifier s2) {
		return s1.equals(s2);
	}

	public static ScopesEqual create(SourceSection source, TermBuild s1, TermBuild s2) {
		return ScopeNodeFactories.createScopesEquals(source, s1, s2);
	}

}
