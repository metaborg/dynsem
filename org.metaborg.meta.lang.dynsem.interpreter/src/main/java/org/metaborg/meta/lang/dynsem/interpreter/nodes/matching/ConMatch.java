package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ConMatch extends MatchPattern {

	private final String name;
	@Children private final MatchPattern[] children;

	public ConMatch(String name, MatchPattern[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	@Override
	public boolean executeMatch(VirtualFrame frame, Object t) {
		final DynSemContext ctx = getContext();
		final ITermRegistry termReg = ctx.getTermRegistry();
		final Class<?> termClass = termReg.getConstructorClass(name, children.length);

		MatchPattern matcher = InterpreterUtils.notNull(ctx, termReg.lookupMatchFactory(termClass), this)
				.apply(getSourceSection(), cloneNodes(children));

		return replace(matcher).executeMatch(frame, t);
	}

	public static ConMatch create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 2);
		String constr = Tools.stringAt(t, 0).stringValue();
		IStrategoList childrenT = Tools.listAt(t, 1);
		MatchPattern[] children = new MatchPattern[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = MatchPattern.create(Tools.applAt(childrenT, i), fd);
		}

		return new ConMatch(constr, children, SourceUtils.dynsemSourceSectionFromATerm(t));
	}
}
