package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface ScopesLayout {

	DynamicObject createScopes();

	boolean isScopes(DynamicObject object);

	boolean isScopes(Object object);

	boolean isScopes(ObjectType objectType);

}
