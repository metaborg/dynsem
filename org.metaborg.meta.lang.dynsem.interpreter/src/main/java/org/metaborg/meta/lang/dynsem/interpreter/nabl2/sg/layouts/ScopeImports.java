package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts;

import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;

public class ScopeImports {
	private ScopeImports() {
	}

	public static final ScopeImports SINGLETON = new ScopeImports();
	private static final Layout LAYOUT = Layout.createLayout();
	private static final Allocator ALLOCATOR = LAYOUT.createAllocator();

	private static class ImportsType extends ObjectType {

	}

	public Shape createShape() {
		return LAYOUT.createShape(new ImportsType());
	}

	// FIXME: should we share an allocator for all ScopeImports?
	public Allocator allocator() {
		return ALLOCATOR;
	}
}
