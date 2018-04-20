package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.arrays.ArrayAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "addr", type = TermBuild.class), @NodeChild(value = "val", type = TermBuild.class) })
public abstract class SetAtAddr extends NativeOpBuild {

	public SetAtAddr(SourceSection source) {
		super(source);
	}

	@Specialization // (guards = { "canSetInFrame(addr, val)" })
	public Object executeFrameSet(FrameAddr addr, Object val) {
		try {
			addr.location().set(addr.frame(), val);
		} catch (IncompatibleLocationException | FinalLocationException e) {
			throw new IllegalStateException(e);
		}
		return val;
	}

	@Specialization
	public Object executeArraySet(ArrayAddr addr, Object val) {
		addr.arr().set(addr.idx(), val);
		return val;
	}

	// protected static boolean canSetInFrame(FrameAddr addr, Object val) {
	// return addr.location().canSet(val);
	// }

	public static SetAtAddr create(SourceSection source, TermBuild addr, TermBuild val) {
		return SetAtAddrNodeGen.create(source, addr, val);
	}
}
