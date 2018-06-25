package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
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

public final class FrameImportLink extends FLink {

	private final Occurrence ref_occ;

	public FrameImportLink(ALabel label, Occurrence ref_occ, DynamicObject frame) {
		this(label, ref_occ, frame, new FrameImportIdentifier(label, ref_occ));
		CompilerAsserts.neverPartOfCompilation();
	}

	public FrameImportLink(ALabel label, Occurrence ref_occ, DynamicObject frame, FrameImportIdentifier edgeIdent) {
		super(label, frame, edgeIdent);
		this.ref_occ = ref_occ;
	}

	public ALabel get_1() {
		return label();
	}

	public DynamicObject get_2() {
		return frame();
	}

	public Occurrence get_3() {
		return occurrence();
	}

	public Occurrence occurrence() {
		return ref_occ;
	}

	@Override
	public int size() {
		return 3;
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
		return "ImportLink(" + frame() + ", " + link() + ")";
	}

	@NodeChildren({ @NodeChild(value = "label", type = TermBuild.class),
			@NodeChild(value="ref_occ", type=TermBuild.class),
			@NodeChild(value = "frm", type = TermBuild.class) })
	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		@Specialization(guards = { "label_cached.equals(label)", "ref_occ_cached.equals(ref_occ)" })
		public FrameImportLink doBuildCached(ALabel label, Occurrence ref_occ, DynamicObject frm,
				@Cached("label") ALabel label_cached, @Cached("ref_occ") Occurrence ref_occ_cached,
				@Cached("createLinkIdentifier(label_cached, ref_occ_cached)") FrameImportIdentifier linkIdentifier) {
			return new FrameImportLink(label, ref_occ, frm, linkIdentifier);
		}
		
		@Specialization
		public FrameImportLink doBuild(ALabel label, Occurrence ref_occ, DynamicObject frm) {
			return new FrameImportLink(label, ref_occ, frm, createLinkIdentifier(label, ref_occ));
		}
		
		protected static FrameImportIdentifier createLinkIdentifier(ALabel label, Occurrence ref_occ) {
			return new FrameImportIdentifier(label, ref_occ);
		}
		
		
	}

	public abstract static class Match extends MatchPattern {

		@Child private MatchPattern label;
		@Child private MatchPattern ref_occ;
		@Child private MatchPattern frm;

		public Match(SourceSection s, MatchPattern label_pat, MatchPattern ref_occ, MatchPattern frm_pat) {
			super(s);
			this.label = label_pat;
			this.ref_occ = ref_occ;
			this.frm = frm_pat;
		}

		@Specialization
		public void doDeepMatch(VirtualFrame frame, FrameImportLink importLink) {
			label.executeMatch(frame, importLink.label());
			ref_occ.executeMatch(frame, importLink.occurrence());
			frm.executeMatch(frame, importLink.frame());
		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object term) {
			if (term instanceof FrameImportLink) {
				doDeepMatch(frame, (FrameImportLink) term);
			} else {
				throw PremiseFailureException.SINGLETON;
			}
		}

	}

}
