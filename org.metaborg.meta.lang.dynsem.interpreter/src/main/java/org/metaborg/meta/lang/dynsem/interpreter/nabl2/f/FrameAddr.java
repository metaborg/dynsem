package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.source.SourceSection;

public final class FrameAddr extends Addr {

	private final DynamicObject frame;
	private final Occurrence key;
	private final Location location;

	public FrameAddr(DynamicObject frame, Occurrence key) {
		this(frame, key, FrameUtils.lookupLocation(frame, key));
	}

	public FrameAddr(DynamicObject frame, Occurrence key, Location loc) {
		this.frame = frame;
		this.key = key;
		this.location = loc;
	}

	@Override
	public int size() {
		return 2;
	}

	public DynamicObject get_1() {
		return frame();
	}

	public Occurrence get_2() {
		return key();
	}

	public DynamicObject frame() {
		return frame;
	}

	public Location location() {
		return location;
	}

	public Occurrence key() {
		return key;
	}

	@Override
	public ITermInstanceChecker getCheck() {
		return null;
	}

	@Override
	public boolean hasStrategoTerm() {
		return false;
	}

	@Override
	public IStrategoTerm getStrategoTerm() {
		return null;
	}

	@Override
	public String toString() {
		return "FrameAddr(" + frame + ", " + key + ", [" + location + "])";
	}

	@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class),
			@NodeChild(value = "key", type = TermBuild.class) })
	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		// @Specialization(guards = { "shapeCheck(frm_shape, frm)" })
		// public FrameAddr buildCached(DynamicObject frm, Occurrence key,
		// @Cached("lookupShape(frm)") Shape frm_shape,
		// @Cached("lookupLocation(frm_shape, key)") Location loc) {
		// return new FrameAddr(frm, key, loc);
		// }

		@Specialization // (replaces = "buildCached")
		public FrameAddr buildUncached(DynamicObject frm, Occurrence key) {
			return new FrameAddr(frm, key);
		}

	}

	public abstract static class Match extends MatchPattern {

		@Child private MatchPattern frame_pat;
		@Child private MatchPattern key_pat;

		public Match(SourceSection s, MatchPattern frame_pat, MatchPattern key_pat) {
			super(s);
			this.frame_pat = frame_pat;
			this.key_pat = key_pat;
		}

		@Specialization
		public void doDeepMatch(VirtualFrame frame, FrameAddr addr) {
			frame_pat.executeMatch(frame, addr.frame());
			key_pat.executeMatch(frame, addr.key());
		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object term) {
			if (term instanceof FrameAddr) {
				doDeepMatch(frame, (FrameAddr) term);
			} else {
				throw PremiseFailureException.SINGLETON;
			}
		}

	}

}
