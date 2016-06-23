package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.MapUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "lmap", type = TermBuild.class),
		@NodeChild(value = "rmap", type = TermBuild.class) })
public abstract class MapExtendBuild extends TermBuild {

	public MapExtendBuild(SourceSection source) {
		super(source);
	}

	public static MapExtendBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapExtend", 2);
		TermBuild lmap = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild rmap = TermBuild.create(Tools.applAt(t, 1), fd);

		return MapExtendBuildNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), lmap, rmap);
	}

	@Specialization
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@TruffleBoundary
	public PersistentMap<?, ?> doEvaluated(PersistentMap lmap, PersistentMap rmap) {
		return MapUtils.plus(rmap, lmap);
	}

}
