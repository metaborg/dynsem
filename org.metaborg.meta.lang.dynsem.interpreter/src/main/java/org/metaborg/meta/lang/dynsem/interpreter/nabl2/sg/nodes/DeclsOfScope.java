package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "scope", type = TermBuild.class) })
public abstract class DeclsOfScope extends NativeOpBuild {

	public DeclsOfScope(SourceSection source) {
		super(source);
	}

	@Specialization
	public IListTerm<Occurrence> executeGetDecls(ScopeIdentifier scope) {
		throw new IllegalStateException("Decls of scope not implemented");
	}

	public static DeclsOfScope create(SourceSection source, TermBuild scope) {
		return DeclsOfScopeNodeGen.create(source, scope);
	}

}
