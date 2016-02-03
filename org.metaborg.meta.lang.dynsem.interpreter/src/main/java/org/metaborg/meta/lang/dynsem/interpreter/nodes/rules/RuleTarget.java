package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public class RuleTarget extends Node {

	@Child protected TermBuild rhsNode;
	@Children protected final TermBuild[] componentNodes;

	public RuleTarget(TermBuild rhsNode, TermBuild[] componentNodes,
			SourceSection source) {
		super(source);
		this.rhsNode = rhsNode;
		this.componentNodes = componentNodes;
	}

	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		RuleResult res = new RuleResult();
		res.result = rhsNode.executeGeneric(frame);
		Object[] componentValues = new IStrategoTerm[componentNodes.length];

		for (int i = 0; i < componentNodes.length; i++) {
			componentValues[i] = componentNodes[i].executeGeneric(frame);
		}

		res.components = componentValues;
		return res;
	}

	public static RuleTarget create(IStrategoAppl targetT, FrameDescriptor fd) {
		assert Tools.hasConstructor(targetT, "Target", 2);
		TermBuild rhsNode = TermBuild.create(Tools.termAt(targetT, 0), fd);

		IStrategoList componentsT = Tools.listAt(targetT, 1);
		TermBuild[] componentNodes = new TermBuild[componentsT.size()];
		for (int i = 0; i < componentNodes.length; i++) {
			componentNodes[0] = TermBuild.createFromLabelComp(
					Tools.applAt(componentsT, i), fd);
		}

		return new RuleTarget(rhsNode, componentNodes,
				SourceSectionUtil.fromStrategoTerm(targetT));
	}
}
