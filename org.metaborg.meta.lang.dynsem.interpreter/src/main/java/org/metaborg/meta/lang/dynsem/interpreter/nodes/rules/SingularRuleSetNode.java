package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.source.SourceSection;

@Deprecated
public class SingularRuleSetNode extends RuleSetNode {

	@Child private Rule rule;

	public SingularRuleSetNode(SourceSection source, String arrowName, Class<?> dispatchClass, Rule rule) {
		super(source, arrowName, dispatchClass);
		this.rule = rule;
	}

	@Override
	public RuleResult execute(Object[] arguments) {
		return rule.execute(Truffle.getRuntime().createVirtualFrame(arguments, rule.getFrameDescriptor()));
	}

	@Override
	public List<Rule> getRules() {
		return Collections.unmodifiableList(Arrays.asList(new Rule[] { rule }));
	}

}
