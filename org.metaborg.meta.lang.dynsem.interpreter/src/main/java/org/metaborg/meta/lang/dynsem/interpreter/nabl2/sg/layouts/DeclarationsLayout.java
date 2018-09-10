package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface DeclarationsLayout {

	DynamicObject createDeclarations();

	boolean isDeclarations(DynamicObject object);

	boolean isDeclarations(Object object);

	boolean isDeclarations(ObjectType objectType);
}
