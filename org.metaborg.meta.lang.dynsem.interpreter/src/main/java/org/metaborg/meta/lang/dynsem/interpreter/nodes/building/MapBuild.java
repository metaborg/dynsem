package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MapBuild extends TermBuild {

	public MapBuild(SourceSection source) {
		super(source);
	}

	public static MapBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Map", 1);
		IStrategoList bindsT = Tools.listAt(t, 0);
		if (bindsT.size() == 0) {
			return EmptyMapBuild.create(t);
		}
		if (bindsT.size() == 1) {
			return BindMapBuild.create(t, fd);
		}

		throw new RuntimeException("Unsupported map build term: " + t);
	}

	public static final class EmptyMapBuild extends MapBuild {

		public EmptyMapBuild(SourceSection source) {
			super(source);
		}

		public static EmptyMapBuild create(IStrategoAppl t) {
			assert Tools.hasConstructor(t, "Map", 1);
			assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 0;
			return new EmptyMapBuild(SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return executeMap(frame);
		}

		@Override
		public PersistentMap<?, ?> executeMap(VirtualFrame frame) {
			return create();
		}

		@TruffleBoundary
		private PersistentMap<?, ?> create() {
			return PersistentHashMap.emptyMap();
		}
	}

	public static final class BindMapBuild extends MapBuild {

		@Child private TermBuild keyNode;
		@Child private TermBuild valNode;

		public BindMapBuild(TermBuild keyNode, TermBuild valNode, SourceSection source) {
			super(source);
			this.keyNode = keyNode;
			this.valNode = valNode;
		}

		public static BindMapBuild create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "Map", 1);
			assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 1;
			IStrategoAppl bind = Tools.applAt(Tools.listAt(t, 0), 0);

			assert Tools.hasConstructor(bind, "Bind", 2);
			TermBuild keyNode = TermBuild.create(Tools.applAt(bind, 0), fd);
			TermBuild valNode = TermBuild.create(Tools.applAt(bind, 1), fd);

			return new BindMapBuild(keyNode, valNode, SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return executeMap(frame);
		}

		@Override
		public PersistentMap<?, ?> executeMap(VirtualFrame frame) {
			Object key = keyNode.executeGeneric(frame);
			Object val = valNode.executeGeneric(frame);
			return create(key, val);
		}

		@TruffleBoundary
		private PersistentMap<?, ?> create(Object key, Object val) {
			return PersistentHashMap.emptyMap().plus(key, val);
		}
	}

}
