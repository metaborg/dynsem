package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * A call node used to attempt to find another set of rules to execute for the given term. Typically instances of this
 * node will be held by a {@link JointRuleNode} which will invoke execution on this node when the directly targetted
 * rules have failed.
 * 
 * When this node is called to execute it decides (based on the kind of rule that this node's parent represents) what
 * the lookup strategy is for the alternative rules, if any such strategy exists.
 * 
 * @author vladvergu
 */
public abstract class AAlternativeRuleCallNode extends DynSemNode {

	public AAlternativeRuleCallNode(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(Object[] arguments);

}
