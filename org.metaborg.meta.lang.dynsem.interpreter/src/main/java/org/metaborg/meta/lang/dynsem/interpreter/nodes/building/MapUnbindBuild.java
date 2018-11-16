package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.MapUtils;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "map", type = TermBuild.class), @NodeChild(value = "key", type = TermBuild.class) })
public abstract class MapUnbindBuild extends TermBuild {

	public MapUnbindBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PersistentMap<?, ?> doEvaluated(PersistentMap map, Object key) {
		return MapUtils.remove(map, key);
	}

}
