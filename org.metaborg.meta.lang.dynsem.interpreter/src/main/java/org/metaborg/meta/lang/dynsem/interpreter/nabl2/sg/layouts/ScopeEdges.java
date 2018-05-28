package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;

public class ScopeEdges {
	private ScopeEdges() {
	}

	public static final ScopeEdges SINGLETON = new ScopeEdges();
	private static final Layout LAYOUT = Layout.createLayout();
	private static final Allocator ALLOCATOR = LAYOUT.createAllocator();

	private static class EdgesType extends ObjectType {

	}

	public Shape createShape() {
		return LAYOUT.createShape(new EdgesType());
	}

	// FIXME: should we share an allocator for all ScopeEdges?
	public Allocator allocator() {
		return ALLOCATOR;
	}
}
