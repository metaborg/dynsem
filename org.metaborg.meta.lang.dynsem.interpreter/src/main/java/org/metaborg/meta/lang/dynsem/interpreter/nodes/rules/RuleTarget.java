package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public class RuleTarget extends Node {

	@Child protected TermBuild rhsNode;
	@Children protected final TermBuild[] componentNodes;
	private final SourceSection sourceSection;

	public RuleTarget(TermBuild rhsNode, TermBuild[] componentNodes, SourceSection source) {
		this.sourceSection = source;
		this.rhsNode = rhsNode;
		this.componentNodes = componentNodes;
	}

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object result = rhsNode.executeGeneric(frame);

		CompilerAsserts.compilationConstant(componentNodes.length);

		Object[] componentValues = new Object[componentNodes.length];

		for (int i = 0; i < componentNodes.length; i++) {
			componentValues[i] = componentNodes[i].executeGeneric(frame);
		}

		return new RuleResult(result, componentValues);
	}

	public static RuleTarget create(IStrategoAppl targetT, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(targetT, "Target", 2);
		TermBuild rhsNode = TermBuild.create(Tools.termAt(targetT, 0), fd);

		IStrategoList componentsT = Tools.listAt(targetT, 1);
		TermBuild[] componentNodes = new TermBuild[componentsT.size()];
		for (int i = 0; i < componentNodes.length; i++) {
			componentNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(componentsT, i), fd);
		}

		return new RuleTarget(rhsNode, componentNodes, SourceSectionUtil.fromStrategoTerm(targetT));
	}
}
