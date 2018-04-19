package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectFactory;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface FrameLayout {
	DynamicObjectFactory createFrameShape(ScopeIdentifier scope);

	DynamicObject createFrame(DynamicObjectFactory factory);

	ScopeIdentifier getScope(DynamicObjectFactory factory);

	ScopeIdentifier getScope(ObjectType objectType);

	ScopeIdentifier getScope(DynamicObject object);

	boolean isFrame(DynamicObject object);

	boolean isFrame(Object object);

	boolean isFrame(ObjectType objectType);

}
