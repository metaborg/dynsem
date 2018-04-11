package org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ScopeGraphLayout {
	// G : Map(Scope, ScopeEntry) // scopes
	// * Map(Occurrence, DeclEntry) // decls
	// * Map(Occurrence, RefEntry) // refs
	// -> ScopeGraph
	//
	// SE : List(Occurrence) // decls
	// * List(Occurrence) // refs
	// * Map(Label, List(Scope)) // direct edges
	// * Map(Label, List(Occurrence)) // import edges
	// -> ScopeEntry
	//
	// DE : List(Scope) // decl scope
	// * Map(Label, List(Scope)) // assoc scopes
	// -> DeclEntry
	//
	// RE : List(Scope) // ref scope
	// -> RefEntry

	// DynamicObject createScopeGraph(DynamicObject scopes, DynamicObject declarations, DynamicObject references);
	DynamicObject createScopeGraph(DynamicObject scopes, DynamicObject declarations);

	DynamicObject getScopes(DynamicObject object);

	DynamicObject getDeclarations(DynamicObject object);
	//
	// DynamicObject getReferences(DynamicObject object);
}
