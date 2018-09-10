package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ReferencesLayout {
	DynamicObject createReferences();

	boolean isReferences(DynamicObject object);

	boolean isReferences(Object object);

	boolean isReferences(ObjectType objectType);
}
