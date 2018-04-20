package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;

public abstract class PathStep extends Node {
	protected final ScopeIdentifier scopeIdent;

	public PathStep(ScopeIdentifier scopeIdent) {
		this.scopeIdent = scopeIdent;
	}

	public abstract Occurrence getTargetDec();

	public abstract FrameAddr executeLookup(DynamicObject frm);

	protected Shape lookupShape(DynamicObject frm) {
		CompilerAsserts.neverPartOfCompilation();
		assert FrameLayoutImpl.INSTANCE.isFrame(frm);
		return frm.getShape();
	}

	protected boolean shapeCheck(Shape shape, DynamicObject frm) {
		return shape != null && shape.check(frm);
	}

	protected Location lookupLocation(Shape shape, Object key) {
		CompilerAsserts.neverPartOfCompilation();
		Property property = shape.getProperty(key);
		if (property == null) {
			throw new ReductionFailure(
					"Slot " + key + " does not exist for frame of scope "
							+ FrameLayoutImpl.INSTANCE.getScope(shape.createFactory()),
					InterpreterUtils.createStacktrace());
		}
		return property.getLocation();
	}

}
