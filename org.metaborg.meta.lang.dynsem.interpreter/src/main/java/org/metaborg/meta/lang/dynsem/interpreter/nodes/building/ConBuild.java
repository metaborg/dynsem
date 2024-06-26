package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ConBuild extends TermBuild {

	private final String name;

	@Children private final TermBuild[] children;

	public ConBuild(String name, TermBuild[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	public static ConBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 2);
		String constr = Tools.stringAt(t, 0).stringValue();

		IStrategoList childrenT = Tools.listAt(t, 1);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}
		return ConBuildNodeGen.create(constr, children, SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Specialization
	public IApplTerm executeSpecialize(VirtualFrame frame) {
		final ITermRegistry termReg = getContext().getTermRegistry();
		final Class<?> termClass = termReg.getConstructorClass(name, children.length);

		final TermBuild build = InterpreterUtils.notNull(getContext(), termReg.lookupBuildFactory(termClass), this)
				.apply(getSourceSection(), cloneNodes(children));

		return replace(build).executeIApplTerm(frame);
	}

}
