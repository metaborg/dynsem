package org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ScopeGraphLayout {

	DynamicObject createScopeGraph(DynamicObject scopes, DynamicObject declarations, DynamicObject references);

	DynamicObject getScopes(DynamicObject object);

	DynamicObject getDeclarations(DynamicObject object);

	DynamicObject getReferences(DynamicObject object);
}
