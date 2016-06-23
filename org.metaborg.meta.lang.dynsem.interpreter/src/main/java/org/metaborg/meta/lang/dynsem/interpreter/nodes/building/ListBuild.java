package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ListBuild extends TermBuild {

	@Children private final TermBuild[] elemNodes;
	@Child private TermBuild tailNode;
	private final Class<?> listClass;

	public ListBuild(SourceSection source, TermBuild[] elemNodes, TermBuild tailNode, Class<?> listClass) {
		super(source);
		this.elemNodes = elemNodes;
		this.tailNode = tailNode;
		this.listClass = listClass;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		final TermBuild concreteListBuild = InterpreterUtils
				.notNull(getContext().getTermRegistry().lookupBuildFactory(listClass))
				.apply(getSourceSection(), cloneNodes(elemNodes), cloneNode(tailNode));
		return replace(concreteListBuild).executeGeneric(frame);
	}

	public static ListBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3);

		IStrategoList elemTs = Tools.listAt(t, 0);
		final TermBuild[] elemNodes = new TermBuild[elemTs.size()];
		for (int i = 0; i < elemNodes.length; i++) {
			elemNodes[i] = TermBuild.create(Tools.applAt(elemTs, i), fd);
		}

		TermBuild tailNodes = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			tailNodes = TermBuild.create(Tools.applAt(t, 1), fd);
		}

		final String dispatchClassName = Tools.javaStringAt(t, Tools.hasConstructor(t, "TypedList", 2) ? 1 : 2);
		Class<?> dispatchClass;

		try {
			dispatchClass = Rule.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		return new ListBuild(SourceSectionUtil.fromStrategoTerm(t), elemNodes, tailNodes, dispatchClass);
	}

}
