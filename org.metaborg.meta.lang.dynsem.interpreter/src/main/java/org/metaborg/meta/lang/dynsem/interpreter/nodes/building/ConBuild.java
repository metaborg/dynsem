package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ConBuild extends TermBuild {

	private final String name;
	private final String sort;

	@Children private TermBuild[] childrenBuilds;

	public ConBuild(SourceSection source, String name, String sort, TermBuild[] childrenBuilds) {
		super(source);
		this.sort = sort;
		this.name = name;
		this.childrenBuilds = childrenBuilds;
	}

	@Specialization
	@ExplodeLoop
	public ApplTerm doSimple(VirtualFrame frame) {
		Object[] children = new Object[childrenBuilds.length];
		for (int i = 0; i < children.length; i++) {
			children[i] = childrenBuilds[i].executeGeneric(frame);
		}
		return new ApplTerm(sort, name, children);
	}

	public static ConBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 3);
		String constr = Tools.stringAt(t, 0).stringValue();

		IStrategoList childrenT = Tools.listAt(t, 1);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}

		return ConBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), constr,
				Tools.applAt(t, 2).toString(),
				children);
	}


}
