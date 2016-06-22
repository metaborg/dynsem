package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.SortRulesUnionNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.SortRulesUnionNodeGen;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RuleUnionNode extends DynSemNode {

	@Children private final Rule[] rules;
	@Child private SortRulesUnionNode fallbackRulesNode;

	private final String arrowName;
	private final Class<?> dispatchClass;

	public RuleUnionNode(SourceSection source, String arrowName, Class<?> dispatchClass, Rule[] rules) {
		super(source);
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
		this.rules = rules;
		this.fallbackRulesNode = SortRulesUnionNodeGen.create(source, arrowName);
	}

	public RuleResult execute(final Object[] arguments) {
		try {
			return executeMainRules(arguments);
		} catch (PatternMatchFailure pmfx) {
			return fallbackRulesNode.execute(arguments[0], arguments);
		}
	}

	@ExplodeLoop
	private RuleResult executeMainRules(final Object[] arguments) {
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

	public Rule[] getRules() {
		return rules;
	}

	public SortRulesUnionNode getSortRulesNode() {
		return fallbackRulesNode;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + arrowName + "->";
	}

}
