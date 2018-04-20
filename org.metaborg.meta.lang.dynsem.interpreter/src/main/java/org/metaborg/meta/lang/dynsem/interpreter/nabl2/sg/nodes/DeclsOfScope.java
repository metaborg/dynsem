package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

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

	@Specialization(guards = { "scope == scope_cached" })
	public Object executeCachedFull(ScopeIdentifier scope, @Cached("scope") ScopeIdentifier scope_cached,
			@Cached(value = "lookupScopeDecls(scope)", dimensions = 1) Occurrence[] decs_cached,
			@Cached("createListBuild()") TermBuild listBuild,
			@Cached("createList(listBuild, decs_cached)") Object list_cached) {
		return list_cached;
	}

	@Specialization(replaces = "executeCachedFull")
	public Object executeCachedFull(ScopeIdentifier scope, @Cached("createListBuild()") TermBuild listBuild) {
		return createList(listBuild, lookupScopeDecls(scope));
	}

	protected Occurrence[] lookupScopeDecls(ScopeIdentifier scope) {
		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());

		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE.getScopes(sg);
		// FIXME eliminate this cast
		return ScopeEntryLayoutImpl.INSTANCE.getDeclarations((DynamicObject) scopes.get(scope));
	}

	protected TermBuild createListBuild() {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		return registry.lookupBuildFactory(registry.getListClass(Occurrence.class)).apply(getSourceSection(),
				new Object[] { new TermBuild[0], null });
	}

	protected Object createList(TermBuild listBuild, Occurrence[] decs) {
		return listBuild.executeEvaluated(null, new Object[] { decs, null });
	}

	public static DeclsOfScope create(SourceSection source, TermBuild scope) {
		return DeclsOfScopeNodeGen.create(source, scope);
	}

}
