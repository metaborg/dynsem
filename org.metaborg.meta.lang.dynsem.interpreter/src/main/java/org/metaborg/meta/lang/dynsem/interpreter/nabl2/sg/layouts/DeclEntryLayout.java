package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface DeclEntryLayout {
	DynamicObject createDeclEntry(ScopeIdentifier[] declarationScopes, DynamicObject associatedScopes);

	ScopeIdentifier[] getDeclarationScopes(DynamicObject object);

	DynamicObject getAssociatedScopes(DynamicObject object);

	boolean isDeclEntry(DynamicObject object);

	boolean isDeclEntry(Object object);

	boolean isDeclEntry(ObjectType objectType);
}
