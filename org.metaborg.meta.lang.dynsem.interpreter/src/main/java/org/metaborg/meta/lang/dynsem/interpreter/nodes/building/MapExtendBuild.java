package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.MapUtils;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "lmap", type = TermBuild.class),
		@NodeChild(value = "rmap", type = TermBuild.class) })
public abstract class MapExtendBuild extends TermBuild {

	public MapExtendBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PersistentMap<?, ?> doEvaluated(PersistentMap lmap, PersistentMap rmap) {
		return MapUtils.plus(rmap, lmap);
	}

}
