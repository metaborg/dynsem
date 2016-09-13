package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
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

public class TupleBuild extends TermBuild {

	@Children private final TermBuild[] elemNodes;
	private final Class<?> tupleClass;

	public TupleBuild(SourceSection source, TermBuild[] elemNodes, Class<?> tupleClass) {
		super(source);
		this.elemNodes = elemNodes;
		this.tupleClass = tupleClass;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		final TermBuild concreteListBuild = InterpreterUtils
				.notNull(getContext().getTermRegistry().lookupBuildFactory(tupleClass))
				.apply(getSourceSection(), cloneNodes(elemNodes));
		return replace(concreteListBuild).executeGeneric(frame);
	}

	public static TupleBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedTuple", 2);

		IStrategoList childrenT = Tools.listAt(t, 0);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}

		String dispatchClassName = Tools.stringAt(t, 1).stringValue();
		Class<?> dispatchClass;

		try {
			dispatchClass = Rule.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}
		return new TupleBuild(SourceSectionUtil.fromStrategoTerm(t), children, dispatchClass);
	}

}
