package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public class JointRuleRoot extends RootNode {

	@Child private JointRuleNode jointNode;

	public JointRuleRoot(SourceSection source, RuleKind kind, String arrowName, Class<?> dispatchClass, Rule[] rules) {
		super(DynSemLanguage.class, source, new FrameDescriptor());
		this.jointNode = new JointRuleNode(source, kind, arrowName, dispatchClass, rules);

		Truffle.getRuntime().createCallTarget(this);
	}

	public JointRuleNode getJointNode() {
		return jointNode;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return jointNode.execute(frame.getArguments());
	}

	@TruffleBoundary
	public String toString() {
		return jointNode.toString();
	}

}
