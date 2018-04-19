package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;

public final class FrameAddr implements Addr {

	private final DynamicObject frame;
	private final Location location;

	public FrameAddr(DynamicObject frame, Location location) {
		this.frame = frame;
		this.location = location;
	}


}
