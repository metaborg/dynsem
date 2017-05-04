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

@NodeChildren({ @NodeChild(value = "map", type = TermBuild.class), @NodeChild(value = "key", type = TermBuild.class) })
public abstract class MapUnbindBuild extends TermBuild {

	public MapUnbindBuild(SourceSection source) {
		super(source);
	}

	public static MapUnbindBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapUnbind", 2);
		TermBuild map = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild key = TermBuild.create(Tools.applAt(t, 1), fd);

		return MapUnbindBuildNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), map, key);
	}

	@Specialization
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@TruffleBoundary
	public PersistentMap<?, ?> doEvaluated(PersistentMap map, Object key) {
		return MapUtils.remove(map, key);
	}

}
