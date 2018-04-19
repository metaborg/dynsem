package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.Path;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class),
		@NodeChild(value = "occurrence", type = TermBuild.class) })
public abstract class Lookup extends NativeOpBuild {

	public Lookup(SourceSection source) {
		super(source);
	}

	// FIXME: this is where we need to be very careful w.r.t. object languages because the Path stored in resolution may
	// not be correct (method overriding)

	// FIXME: this is the place to cache the lookup. if the ref is constant and the frame shape is constant then teh
	// Location part of teh frameaddr will also be constant and then we don't need to reevaluate the entire chain
	@Specialization(guards = { "ref.equals(ref_cached)" })
	public FrameAddr executeCachedDirect(DynamicObject frm, Occurrence ref, @Cached("ref") Occurrence ref_cached,
			@Cached("create(lookupPathResolver(ref_cached))") DirectCallNode resolverNode) {
		return (FrameAddr) resolverNode.call(new Object[] { frm });
	}

	@Specialization(replaces = "executeCachedDirect")
	public FrameAddr executeIndirect(DynamicObject frm, Occurrence ref,
			@Cached("create()") IndirectCallNode resolverNode) {
		return (FrameAddr) resolverNode.call(lookupPathResolver(ref), new Object[] { frm });
	}

	protected CallTarget lookupPathResolver(Occurrence ref) {
		Object p = NaBL2LayoutImpl.INSTANCE.getNameResolution(getContext().getNaBL2Solution()).get(ref);
		if (p == null) {
			throw new ReductionFailure("Unresolved reference: " + ref, InterpreterUtils.createStacktrace());
		}
		assert p instanceof Path;
		return ((Path) p).getCallTarget();
	}

	public static Lookup create(SourceSection source, TermBuild frm, TermBuild occurrence) {
		return LookupNodeGen.create(source, frm, occurrence);
	}

}
