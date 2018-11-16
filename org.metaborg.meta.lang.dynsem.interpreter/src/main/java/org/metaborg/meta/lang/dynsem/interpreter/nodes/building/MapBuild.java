package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MapBuild extends TermBuild {

	public MapBuild(SourceSection source) {
		super(source);
	}

	public abstract static class EmptyMapBuild extends MapBuild {

		public EmptyMapBuild(SourceSection source) {
			super(source);
		}

		@Specialization
		public PersistentMap<?, ?> executeEmpty() {
			return create();
		}

		@TruffleBoundary
		private PersistentMap<?, ?> create() {
			return PersistentHashMap.emptyMap();
		}
	}

	@NodeChildren({ @NodeChild(value = "key", type = TermBuild.class),
			@NodeChild(value = "val", type = TermBuild.class) })
	public static abstract class BindMapBuild extends MapBuild {

		public BindMapBuild(SourceSection source) {
			super(source);
		}

		@Specialization
		public PersistentMap<?, ?> executeBind(Object key, Object val) {
			return doCreateAndBind(key, val);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@TruffleBoundary
		private PersistentMap<?, ?> doCreateAndBind(Object key, Object val) {
			return ((TransientMap) PersistentHashMap.EMPTY.asTransient()).plus(key, val).persist();
		}

	}

}
