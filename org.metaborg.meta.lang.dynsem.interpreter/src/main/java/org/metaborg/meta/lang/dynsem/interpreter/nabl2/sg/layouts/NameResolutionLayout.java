package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface NameResolutionLayout {
	DynamicObject createNameResolution();

	boolean isNameResolution(DynamicObject object);

	boolean isNameResolution(Object object);

	boolean isNameResolution(ObjectType objectType);
}
