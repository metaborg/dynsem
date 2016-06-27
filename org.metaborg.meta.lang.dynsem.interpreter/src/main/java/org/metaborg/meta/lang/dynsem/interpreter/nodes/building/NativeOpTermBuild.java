package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A generic node for a NativeOp cons. This node only contains the necessary logic to replace itself with a specific
 * node which implements the functionality. That replacement node must be registered in the {@link ITermRegistry}
 * associated with the context.
 * 
 * @author vladvergu
 *
 */
public class NativeOpTermBuild extends TermBuild {

	protected final String constr;
	@Children protected final TermBuild[] children;

	public NativeOpTermBuild(String constr, TermBuild[] children, SourceSection source) {
		super(source);
		this.constr = constr;
		this.children = children;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		final ITermRegistry termReg = getContext().getTermRegistry();
		final Class<?> termClass = termReg.getNativeOperatorClass(constr, children.length);

		TermBuild build = InterpreterUtils.notNull(termReg.lookupNativeOpBuildFactory(termClass))
				.apply(getSourceSection(), children);
		return replace(build).executeGeneric(frame);
	}

	public static NativeOpTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "NativeOp", 2);
		String constr = Tools.stringAt(t, 0).stringValue();

		IStrategoList childrenT = Tools.listAt(t, 1);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}

		return new NativeOpTermBuild(constr, children, SourceSectionUtil.fromStrategoTerm(t));

	}

}
