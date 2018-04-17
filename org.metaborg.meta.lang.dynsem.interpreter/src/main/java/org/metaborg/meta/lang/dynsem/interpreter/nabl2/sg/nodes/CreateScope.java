package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.github.krukow.clj_lang.IPersistentMap;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "scopeIdent", type = TermBuild.class),
		@NodeChild(value = "decs", type = TermBuild.class), @NodeChild(value = "refs", type = TermBuild.class),
		@NodeChild(value = "edges", type = TermBuild.class), @NodeChild(value = "imports", type = TermBuild.class) })
public abstract class CreateScope extends NativeOpBuild {
	public CreateScope(SourceSection source) {
		super(source);
	}

	@Specialization
	public DynamicObject executeCreate(ScopeIdentifier scopeIdent, IListTerm<?> decs, IListTerm<?> refs,
			IPersistentMap<?, ?> edges, IPersistentMap<?, ?> imports) {
		throw new RuntimeException("Scope creation not implemented");
	}

	public static CreateScope create(SourceSection source, TermBuild scopeIdent, TermBuild decs, TermBuild refs,
			TermBuild edges, TermBuild imports) {
		return CreateScopeNodeGen.create(source, scopeIdent, decs, refs, edges, imports);
	}
}
