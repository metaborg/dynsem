package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface FrameFactoriesLayout {
	DynamicObject createFrameFactories();

	boolean isFrameFactories(DynamicObject object);

	boolean isFrameFactories(Object object);

	boolean isFrameFactories(ObjectType objectType);
}
