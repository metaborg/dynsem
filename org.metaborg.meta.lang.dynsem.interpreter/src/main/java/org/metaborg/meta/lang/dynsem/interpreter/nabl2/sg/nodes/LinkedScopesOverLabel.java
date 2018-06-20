package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
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

@NodeChildren({ @NodeChild(value = "scope", type = TermBuild.class),
		@NodeChild(value = "label", type = TermBuild.class) })
public abstract class LinkedScopesOverLabel extends NativeOpBuild {

	public LinkedScopesOverLabel(SourceSection source) {
		super(source);
	}

	// TODO
	// @Specialization(guards = { "scope == scope_cached" })
	// public Object doCached(ScopeIdentifier scope, ALabel label, @Cached("scope") ScopeIdentifier scope_cached,
	// @Cached("label") ALabel label_cached,
	// @Cached(value = "lookupScopes(scope_cached, label_cached)", dimensions = 1) ScopeIdentifier[] scopes_cached,
	// @Cached("createListBuild()") TermBuild listBuild,
	// @Cached("createList(listBuild, scopes_cached)") Object list_cached) {
	// return list_cached;
	// }

	@Specialization // (replaces = "doCached")
	public Object doUncached(ScopeIdentifier scope, ALabel label,
			@Cached("createListConstructor()") ITermInit listConstructor) {
		return listConstructor.apply((Object[]) lookupScopes(scope, label));
	}

	protected ScopeIdentifier[] lookupScopes(ScopeIdentifier scope, ALabel label) {
		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());

		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE.getScopes(sg);
		DynamicObject scopeEntry = (DynamicObject) scopes.get(scope);
		DynamicObject scopeEdges = ScopeEntryLayoutImpl.INSTANCE.getEdges(scopeEntry);
		return (ScopeIdentifier[]) scopeEdges.get(label);
	}

	protected ITermInit createListConstructor() {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		Class<?> listClass = registry.getListClass(ScopeIdentifier.class);
		return registry.lookupClassConstructorWrapper(listClass);
	}

	public static LinkedScopesOverLabel create(SourceSection source, TermBuild scope, TermBuild label) {
		return ScopeNodeFactories.createLinkedScopesOverLabel(source, scope, label);
	}
}
