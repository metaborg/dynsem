package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "dec", type = TermBuild.class) })
public abstract class NewFrameAddr extends NativeOpBuild {

	public NewFrameAddr(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "dec == dec_cached", "shapeCheck(shape, frm)" })
	public FrameAddr createCached(DynamicObject frm, Occurrence dec, @Cached("dec") Occurrence dec_cached,
			@Cached("lookupShape(frm)") Shape shape, @Cached("lookupLocation(shape, dec)") Location loc) {
		return new FrameAddr(frm, loc, dec_cached);
	}

	@Specialization(replaces = "createCached")
	public FrameAddr createUncached(DynamicObject frm, Occurrence dec) {
		return new FrameAddr(frm, frm.getShape().getProperty(dec).getLocation(), dec);
	}

	protected Shape lookupShape(DynamicObject frm) {
		CompilerAsserts.neverPartOfCompilation();
		assert FrameLayoutImpl.INSTANCE.isFrame(frm);
		return frm.getShape();
	}

	protected boolean shapeCheck(Shape shape, DynamicObject frm) {
		return shape != null && shape.check(frm);
	}

	protected Location lookupLocation(Shape shape, Occurrence dec) {
		CompilerAsserts.neverPartOfCompilation();
		Property property = shape.getProperty(dec);
		if (property == null) {
			throw new ReductionFailure(
					"Occurrence " + dec + " does not exist in scope "
							+ FrameLayoutImpl.INSTANCE.getScope(shape.createFactory()),
					InterpreterUtils.createStacktrace(), this);
		}
		return property.getLocation();
	}

	public static NewFrameAddr create(SourceSection source, TermBuild frm, TermBuild dec) {
		return NewFrameAddrNodeGen.create(source, frm, dec);
	}

}
