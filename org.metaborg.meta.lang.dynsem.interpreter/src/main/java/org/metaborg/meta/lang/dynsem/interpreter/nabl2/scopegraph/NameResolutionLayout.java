package org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface NameResolutionLayout {
	DynamicObject createNameResolution();
}
