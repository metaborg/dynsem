package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "scope", type = TermBuild.class) })
public abstract class DeclsOfScope extends NativeOpBuild {

	public DeclsOfScope(SourceSection source) {
		super(source);
	}

	// TODO
	// @Specialization(guards = { "scope == scope_cached" })
	// public Object executeCachedFull(ScopeIdentifier scope, @Cached("scope") ScopeIdentifier scope_cached,
	// @Cached(value = "lookupScopeDecls(scope)", dimensions = 1) Occurrence[] decs_cached,
	// @Cached("createListBuild()") TermBuild listBuild,
	// @Cached("createList(listBuild, decs_cached)") Object list_cached) {
	// return list_cached;
	// }

	@Specialization // (replaces = "executeCachedFull")
	public Object executeCachedFull(ScopeIdentifier scope,
			@Cached("createListConstructor()") ITermInit listConstructor) {
		return listConstructor.apply((Object[]) lookupScopeDecls(scope));
	}

	protected Occurrence[] lookupScopeDecls(ScopeIdentifier scope) {
		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());

		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE.getScopes(sg);
		// FIXME eliminate this cast
		return ScopeEntryLayoutImpl.INSTANCE.getDeclarations((DynamicObject) scopes.get(scope));
	}

	protected ITermInit createListConstructor() {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		Class<?> listClass = registry.getListClass(Occurrence.class);
		return registry.lookupClassConstructorWrapper(listClass);
	}

	public static DeclsOfScope create(SourceSection source, TermBuild scope) {
		return ScopeNodeFactories.createDeclsOfScope(source, scope);
	}

}
