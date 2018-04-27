package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.TupleTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "children", type = TermBuild[].class)
public abstract class TupleBuild extends TermBuild {

	private final String sort;

	public TupleBuild(SourceSection source, String sort) {
		super(source);
		this.sort = sort;
	}

	@Specialization
	public TupleTerm doTuple(Object[] children) {
		return new TupleTerm(sort, children);
	}

	public static TupleBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedTuple", 2);

		IStrategoList childrenT = Tools.listAt(t, 0);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}

		String tupleSort = Tools.stringAt(t, 1).stringValue();
		Class<?> dispatchClass;

		return TupleBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), tupleSort, children);
	}

}
