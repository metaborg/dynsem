package org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.dsl.Layout;

@Layout
public interface NaBL2Layout {
	DynamicObject createNaBL2(DynamicObject scopeGraph, DynamicObject nameResolution, DynamicObject types);

	DynamicObject getScopeGraph(DynamicObject object);

	DynamicObject getNameResolution(DynamicObject object);

	DynamicObject getTypes(DynamicObject object);

}
