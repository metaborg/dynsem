package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public class RelationAppLhs extends Node {

	@Child protected TermExpansion termExpansionNode;
	@Children protected final TermBuild[] roNodes;
	@Children protected final TermBuild[] rwNodes;

	public RelationAppLhs(TermBuild termNode, TermBuild[] roNodes,
			TermBuild[] rwNodes, SourceSection source) {
		super(source);
		this.termExpansionNode = TermExpansionNodeGen.create(termNode);
		this.roNodes = roNodes;
		this.rwNodes = rwNodes;
	}

	public static RelationAppLhs create(IStrategoAppl reads, IStrategoAppl source,
			FrameDescriptor fd) {
		assert Tools.hasConstructor(source, "Source", 2);
		TermBuild lhsNode = TermBuild.create(Tools.applAt(source, 0), fd);

		assert Tools.hasConstructor(reads, "Reads", 1);
		IStrategoList ros = Tools.listAt(reads, 0);
		TermBuild[] roNodes = new TermBuild[ros.getSubtermCount()];
		for (int i = 0; i < roNodes.length; i++) {
			roNodes[i] = TermBuild
					.createFromLabelComp(Tools.applAt(ros, i), fd);
		}

		IStrategoList rws = Tools.listAt(source, 1);
		TermBuild[] rwNodes = new TermBuild[rws.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild
					.createFromLabelComp(Tools.applAt(rws, i), fd);
		}

		return new RelationAppLhs(lhsNode, roNodes, rwNodes,
				SourceSectionUtil.fromStrategoTerm(source));
	}

	@ExplodeLoop
	public Object[] executeObjectArray(VirtualFrame frame) {
		Object[] termBits = termExpansionNode.execute(frame);

		int offset = termBits.length;
		Object[] args = new Object[offset + roNodes.length + rwNodes.length];

		System.arraycopy(termBits, 0, args, 0, offset);

		CompilerAsserts.compilationConstant(roNodes.length);

		for (int i = 0; i < roNodes.length; i++) {
			args[offset + i] = roNodes[i].executeGeneric(frame);
		}

		offset += roNodes.length;

		CompilerAsserts.compilationConstant(rwNodes.length);
		for (int i = 0; i < rwNodes.length; i++) {
			args[offset + i] = rwNodes[i].executeGeneric(frame);
		}

		return args;
	}

}
