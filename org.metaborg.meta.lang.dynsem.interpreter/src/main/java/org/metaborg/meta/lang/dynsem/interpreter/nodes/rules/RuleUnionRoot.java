package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public class RuleUnionRoot extends RootNode {

	@Child private RuleUnionNode unionNode;

	protected RuleUnionRoot(SourceSection source, String arrowName, Rule[] rules) {
		super(DynSemLanguage.class, source, new FrameDescriptor());
		this.unionNode = new RuleUnionNode(source, arrowName, rules);
		Truffle.getRuntime().createCallTarget(this);
	}

	public RuleUnionNode getUnionNode() {
		return unionNode;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return unionNode.execute(frame.getArguments());
	}

}
