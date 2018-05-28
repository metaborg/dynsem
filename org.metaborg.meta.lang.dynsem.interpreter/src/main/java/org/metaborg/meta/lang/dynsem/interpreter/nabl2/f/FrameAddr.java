package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;

public final class FrameAddr implements Addr {

	private final DynamicObject frame;
	private final Location location;
	private final Occurrence key;

	public FrameAddr(DynamicObject frame, Location location, Occurrence key) {
		this.frame = frame;
		this.location = location;
		this.key = key;
	}

	public Location location() {
		return location;
	}

	public DynamicObject frame() {
		return frame;
	}

	public Occurrence key() {
		return key;
	}

}
