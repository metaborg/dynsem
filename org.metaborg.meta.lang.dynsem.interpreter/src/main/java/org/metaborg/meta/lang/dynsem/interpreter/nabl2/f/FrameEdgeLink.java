package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

public final class FrameEdgeLink extends FLink {

	public FrameEdgeLink(ALabel label, DynamicObject frm) {
		this(label, frm, new FrameEdgeIdentifier(label, FrameLayoutImpl.INSTANCE.getScope(frm)));
		CompilerAsserts.neverPartOfCompilation();
	}

	public FrameEdgeLink(ALabel label, DynamicObject frm, FrameEdgeIdentifier edgeIdent) {
		super(label, frm, edgeIdent);
	}

	public ALabel get_1() {
		return label();
	}

	public DynamicObject get_2() {
		return frame();
	}

	@Override
	public int size() {
		return 2;
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
		return "EdgeLink(" + frame() + ", " + link() + ")";
	}

	@NodeChildren({ @NodeChild(value = "label", type = TermBuild.class),
			@NodeChild(value = "frm", type = TermBuild.class) })
	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		@Specialization(guards = { "label_cached.equals(label)", "scope_cached.equals(getFrameScope(frm))" })
		public FrameEdgeLink doBuildCached(ALabel label, DynamicObject frm, @Cached("label") ALabel label_cached,
				@Cached("getFrameScope(frm)") ScopeIdentifier scope_cached,
				@Cached("createLinkIdentifier(label_cached, scope_cached)") FrameEdgeIdentifier linkIdent) {
			return new FrameEdgeLink(label, frm, linkIdent);
		}

		@Specialization
		public FrameEdgeLink doBuild(ALabel label, DynamicObject frm) {
			return new FrameEdgeLink(label, frm, createLinkIdentifier(label, getFrameScope(frm)));
		}

		protected static ScopeIdentifier getFrameScope(DynamicObject frm) {
			return FrameLayoutImpl.INSTANCE.getScope(frm);
		}

		protected static FrameEdgeIdentifier createLinkIdentifier(ALabel label, ScopeIdentifier scope) {
			return new FrameEdgeIdentifier(label, scope);
		}

	}

	public abstract static class Match extends MatchPattern {

		@Child private MatchPattern label;
		@Child private MatchPattern frm;

		public Match(SourceSection s, MatchPattern label_pat, MatchPattern frm_pat) {
			super(s);
			this.label = label_pat;
			this.frm = frm_pat;
		}

		@Specialization
		public void doDeepMatch(VirtualFrame frame, FrameEdgeLink edgeLink) {
			label.executeMatch(frame, edgeLink.label());
			frm.executeMatch(frame, edgeLink.frame());
		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object term) {
			if (term instanceof FrameEdgeLink) {
				doDeepMatch(frame, (FrameEdgeLink) term);
			} else {
				throw PremiseFailureException.SINGLETON;
			}
		}

	}

}
