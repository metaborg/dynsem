package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerDirectives;
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

}
