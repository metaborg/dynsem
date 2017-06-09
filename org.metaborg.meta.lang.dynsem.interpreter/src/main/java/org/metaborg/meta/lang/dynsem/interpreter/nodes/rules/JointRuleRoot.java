package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class JointRuleRoot extends DynSemRootNode {

	@Child private JointRuleNode jointNode;

	public JointRuleRoot(DynSemLanguage lang, SourceSection source, RuleKind kind, String arrowName,
			Class<?> dispatchClass, Rule[] rules) {
		super(lang);
		this.jointNode = new JointRuleNode(source, kind, arrowName, dispatchClass, rules);

		Truffle.getRuntime().createCallTarget(this);
	}

	public JointRuleNode getJointNode() {
		return jointNode;
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		return jointNode.execute(frame.getArguments());
	}

	@TruffleBoundary
	public String toString() {
		return jointNode.toString();
	}

}
