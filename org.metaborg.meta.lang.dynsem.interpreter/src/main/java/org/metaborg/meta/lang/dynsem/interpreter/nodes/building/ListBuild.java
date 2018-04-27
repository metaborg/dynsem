package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListBuild extends TermBuild {

	@Children protected final TermBuild[] elemNodes;
	@Child protected TermBuild tailNode;
	private final String listSort;

	public ListBuild(SourceSection source, TermBuild[] elemNodes, TermBuild tailNode, String listSort) {
		super(source);
		this.elemNodes = elemNodes;
		this.tailNode = tailNode;
		this.listSort = listSort;
	}

	@Specialization(guards = "tailNode == null")
	@ExplodeLoop
	public ListTerm doNoTail(VirtualFrame frame) {
		Object[] elems = new Object[elemNodes.length];
		for (int i = 0; i < elemNodes.length; i++) {
			elems[i] = elemNodes[i].executeGeneric(frame);
		}
		return new ListTerm(listSort, elems, null);
	}

	@Specialization(guards = "tailNode != null")
	@ExplodeLoop
	public ListTerm doWithTail(VirtualFrame frame) {
		Object[] elems = new Object[elemNodes.length];
		for (int i = 0; i < elemNodes.length; i++) {
			elems[i] = elemNodes[i].executeGeneric(frame);
		}

		return tailNode.executeIList(frame).prefix(elems);
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

		final String sort = Tools.javaStringAt(t, Tools.hasConstructor(t, "TypedList", 2) ? 1 : 2);

		return ListBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), elemNodes, tailNodes,
				sort);
	}

}
