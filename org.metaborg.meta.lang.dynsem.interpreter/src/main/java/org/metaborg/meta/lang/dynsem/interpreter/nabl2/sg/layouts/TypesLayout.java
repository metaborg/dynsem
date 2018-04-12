package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface TypesLayout {
	DynamicObject createTypes();

	boolean isTypes(DynamicObject object);

	boolean isTypes(Object object);

	boolean isTypes(ObjectType objectType);
}
