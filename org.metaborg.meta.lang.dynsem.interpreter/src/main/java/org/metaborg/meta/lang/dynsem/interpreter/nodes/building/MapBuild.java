package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MapBuild extends TermBuild {

	public MapBuild(SourceSection source) {
		super(source);
	}

	public static MapBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Map_", 1);
		IStrategoList bindsT = Tools.listAt(t, 0);
		if (bindsT.size() == 0) {
			return EmptyMapBuild.create(t);
		}
		if (bindsT.size() == 1) {
			return BindMapBuild.create(t, fd);
		}

		throw new RuntimeException("Unsupported map build term: " + t);
	}

	public abstract static class EmptyMapBuild extends MapBuild {

		public EmptyMapBuild(SourceSection source) {
			super(source);
		}

		public static EmptyMapBuild create(IStrategoAppl t) {
			assert Tools.hasConstructor(t, "Map_", 1);
			assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 0;
			return MapBuildFactory.EmptyMapBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
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

		public static BindMapBuild create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "Map_", 1);
			assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 1;
			IStrategoAppl bind = Tools.applAt(Tools.listAt(t, 0), 0);

			assert Tools.hasConstructor(bind, "Bind", 2);
			TermBuild keyNode = TermBuild.create(Tools.applAt(bind, 0), fd);
			TermBuild valNode = TermBuild.create(Tools.applAt(bind, 1), fd);

			return MapBuildFactory.BindMapBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), keyNode,
					valNode);
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
