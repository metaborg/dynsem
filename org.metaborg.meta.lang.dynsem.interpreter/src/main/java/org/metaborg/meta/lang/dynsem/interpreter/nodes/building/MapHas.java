package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.MapUtils;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "mapNode", type = TermBuild.class),
		@NodeChild(value = "keyNode", type = TermBuild.class) })
public abstract class MapHas extends TermBuild {

	public MapHas(SourceSection source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Specialization
	public boolean doEvaluated(@SuppressWarnings("rawtypes") PersistentMap map, Object key) {
		return MapUtils.has(map, key);
	}

}
