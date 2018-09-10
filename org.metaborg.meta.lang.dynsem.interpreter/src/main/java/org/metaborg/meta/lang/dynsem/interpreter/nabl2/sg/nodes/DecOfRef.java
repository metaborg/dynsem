package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.Path;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "ref", type = TermBuild.class)
public abstract class DecOfRef extends NativeOpBuild {

	public DecOfRef(SourceSection source) {
		super(source);
	}

	@Specialization(guards = { "ref == ref_cached" })
	public Occurrence getCached(Occurrence ref, @Cached("ref") Occurrence ref_cached,
			@Cached("getUncached(ref_cached)") Occurrence dec) {
		return dec;
	}

	@Specialization // (replaces = "getCached")
	public Occurrence getUncached(Occurrence ref) {
		return ((Path) NaBL2LayoutImpl.INSTANCE.getNameResolution(getContext().getNaBL2Solution()).get(ref))
				.getTargetDec();
	}

	public static DecOfRef create(SourceSection source, TermBuild ref) {
		return ScopeNodeFactories.createDecOfRef(source, ref);
	}

}
