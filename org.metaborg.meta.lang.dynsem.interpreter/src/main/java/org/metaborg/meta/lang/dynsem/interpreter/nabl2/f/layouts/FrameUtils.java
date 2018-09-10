package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;

public final class FrameUtils {

	public static Layout layout() {
		return FrameLayoutImpl.LAYOUT;
	}


	public static Shape lookupShape(DynamicObject frm) {
		assert FrameLayoutImpl.INSTANCE.isFrame(frm);
		return frm.getShape();
	}

	public static boolean shapeCheck(Shape shape, DynamicObject frm) {
		return shape != null && shape.check(frm);
	}

	public static Location lookupLocation(DynamicObject frm, Occurrence key) {
		return lookupLocation(frm.getShape(), key);
	}

	public static Location lookupLocation(Shape shape, Object key) {
		CompilerAsserts.neverPartOfCompilation();
		Property property = shape.getProperty(key);
		if (property == null) {
			throw new IllegalStateException("Slot " + key + " does not exist for frame of scope "
					+ FrameLayoutImpl.INSTANCE.getScope(shape.createFactory()));
		}
		return property.getLocation();
	}
}
