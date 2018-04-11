package org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ScopeEntryLayout {
	DynamicObject createScopeEntry(Occurrence[] declarations, Occurrence[] references, DynamicObject edges,
			DynamicObject imports);

	Occurrence[] getDeclarations(DynamicObject object);

	Occurrence[] getReferences(DynamicObject object);

	DynamicObject getEdges(DynamicObject object);

	DynamicObject getImports(DynamicObject object);

	boolean isScopeEntry(DynamicObject object);

	boolean isScopeEntry(Object object);

	boolean isScopeEntry(ObjectType objectType);
}
