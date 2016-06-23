package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class MultiRuleUnionNode extends RuleUnionNode {

	@Children private final Rule[] rules;

	public MultiRuleUnionNode(SourceSection source, String arrowName, Class<?> dispatchClass, Rule[] rules) {
		super(source, arrowName, dispatchClass);
		this.rules = rules;
	}

	@ExplodeLoop
	public RuleResult execute(final Object[] arguments) {
		CompilerAsserts.compilationConstant(rules.length);

		for (int i = 0; i < rules.length; i++) {
			try {
				final Rule r = rules[i];
				return r.execute(Truffle.getRuntime().createVirtualFrame(arguments, r.getFrameDescriptor()));
			} catch (PatternMatchFailure pmfx) {
				;
			}
		}
		// there are no rules or all rules failed. we throw a soft exception to allow sort-based rules to be tried
		throw PatternMatchFailure.INSTANCE;

	}

	@Override
	public List<Rule> getRules() {
		return Collections.unmodifiableList(Arrays.asList(rules));
	}

}
