package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ScopeGraphLayout {

	DynamicObject createScopeGraph(DynamicObject scopes, DynamicObject declarations, DynamicObject references);

	DynamicObject getScopes(DynamicObject object);

	DynamicObject getDeclarations(DynamicObject object);

	DynamicObject getReferences(DynamicObject object);

	boolean isScopeGraph(DynamicObject object);

	boolean isScopeGraph(Object object);

	boolean isScopeGraph(ObjectType objectType);
}
