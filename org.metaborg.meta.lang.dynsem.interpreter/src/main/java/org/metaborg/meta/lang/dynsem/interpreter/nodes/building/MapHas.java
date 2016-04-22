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

@NodeChildren({ @NodeChild(value = "mapNode", type = TermBuild.class),
		@NodeChild(value = "keyNode", type = TermBuild.class) })
public abstract class MapHas extends TermBuild {

	public static MapHas create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapHas", 2);
		TermBuild mapNode = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild keyNode = TermBuild.create(Tools.applAt(t, 1), fd);
		return MapHasNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), mapNode, keyNode);
	}

	public MapHas(SourceSection source) {
		super(source);
	}

	@Specialization
	@TruffleBoundary
	public boolean doEvaluated(@SuppressWarnings("rawtypes") PersistentMap map, Object key) {
		return map.containsKey(key);
	}

}
