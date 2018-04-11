package org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface DeclEntryLayout {
	DynamicObject createDeclEntry(ScopeIdentifier[] declarationScopes, DynamicObject associatedScopes);

	ScopeIdentifier[] getDeclarationScopes(DynamicObject object);

	DynamicObject getAssociatedScopes(DynamicObject object);
}
