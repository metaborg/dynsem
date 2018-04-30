package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ConsNilList;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "mapNode", type = TermBuild.class)
public abstract class TypedMapKeys extends TermBuild {

	private final String keyListSort;

	public static TypedMapKeys create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedMapKeys", 2);
		TermBuild mapNode = TermBuild.create(Tools.applAt(t, 0), fd);
		String keyListSort = Tools.javaStringAt(t, 1);
		return TypedMapKeysNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), keyListSort, mapNode);
	}

	public TypedMapKeys(SourceSection source, String keylistClass) {
		super(source);
		this.keyListSort = keylistClass;
	}

	@Specialization
	public ConsNilList doList(@SuppressWarnings("rawtypes") PersistentMap map) {
		return ConsNilList.fromArray(keyListSort, keysOf(map), null);
	}

	@TruffleBoundary
	private Object[] keysOf(@SuppressWarnings("rawtypes") PersistentMap map) {
		return map.keySet().toArray();
	}

}
