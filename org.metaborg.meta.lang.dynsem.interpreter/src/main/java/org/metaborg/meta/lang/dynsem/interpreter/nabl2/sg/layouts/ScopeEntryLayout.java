package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ScopeEntryLayout {
	DynamicObject createScopeEntry(ScopeIdentifier identifier, Occurrence[] declarations, Occurrence[] references,
			DynamicObject edges,
			DynamicObject imports);

	ScopeIdentifier getIdentifier(DynamicObject object);

	Occurrence[] getDeclarations(DynamicObject object);

	Occurrence[] getReferences(DynamicObject object);

	DynamicObject getEdges(DynamicObject object);

	DynamicObject getImports(DynamicObject object);

	boolean isScopeEntry(DynamicObject object);

	boolean isScopeEntry(Object object);

	boolean isScopeEntry(ObjectType objectType);


}
