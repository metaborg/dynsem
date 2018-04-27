package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "mapNode", type = TermBuild.class)
public abstract class TypedMapValues extends TermBuild {

	private final String valueListSort;

	public static TypedMapValues create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedMapValues", 2);
		TermBuild mapNode = TermBuild.create(Tools.applAt(t, 0), fd);
		String valueListSort = Tools.javaStringAt(t, 1);
		return TypedMapValuesNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), valueListSort, mapNode);
	}

	public TypedMapValues(SourceSection source, String valueListSort) {
		super(source);
		this.valueListSort = valueListSort;
	}

	@Specialization
	public ListTerm doList(@SuppressWarnings("rawtypes") PersistentMap map) {
		return new ListTerm(valueListSort, map.values().toArray(), null);
	}

}
