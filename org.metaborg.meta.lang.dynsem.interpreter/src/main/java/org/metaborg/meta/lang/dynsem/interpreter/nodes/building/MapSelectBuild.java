package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

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
public abstract class MapSelectBuild extends TermBuild {

	public MapSelectBuild(SourceSection source) {
		super(source);
	}

	public static MapSelectBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapSelect", 2);
		TermBuild map = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild key = TermBuild.create(Tools.applAt(t, 1), fd);

		return MapSelectBuildNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), map, key);
	}

	@Specialization
	@SuppressWarnings({ "rawtypes" })
	@TruffleBoundary
	public Object doEvaluated(PersistentMap map, Object key) {
		Object res = map.get(key);
		if (res != null) {
			return res;
		}
		throw new IllegalStateException("No map entry for key: " + key);
	}

}
