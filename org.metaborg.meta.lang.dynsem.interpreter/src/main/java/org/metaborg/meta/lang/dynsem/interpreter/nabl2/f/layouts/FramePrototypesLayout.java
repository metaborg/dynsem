package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface FramePrototypesLayout {
	DynamicObject createFramePrototypes();

	boolean isFramePrototypes(DynamicObject object);

	boolean isFramePrototypes(Object object);

	boolean isFramePrototypes(ObjectType objectType);
}
