package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleUnionNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class VariableUnionNode extends DynSemNode {

	protected final String arrowName;

	public static VariableUnionNode create(SourceSection source, String arrowName) {
		return new _Uninitialized(source, arrowName);
	}

	public VariableUnionNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Object[] arguments);

	private static final class _Uninitialized extends VariableUnionNode {

		public _Uninitialized(SourceSection source, String arrowName) {
			super(source, arrowName);
		}

		@Override
		public RuleResult execute(Object[] arguments) {
			RuleUnionNode ruleUnion = getContext().getRuleRegistry().lookupRules(arrowName, arguments[0].getClass())
					.getUnionNode();
			return replace(
					new _Initialized(getSourceSection(), arrowName, NodeUtil.cloneNode(ruleUnion.getSortRulesNode())))
							.executeEvaluated(arguments, ruleUnion.getRules());
		}
	}

	private static final class _Initialized extends VariableUnionNode {

		@Child private SortRulesUnionNode sortUnionNode;

		public _Initialized(SourceSection source, String arrowName, SortRulesUnionNode sortUnionNode) {
			super(source, arrowName);
			this.sortUnionNode = sortUnionNode;
		}

		@Override
		public RuleResult execute(Object[] arguments) {
			RuleUnionNode ruleUnion = getContext().getRuleRegistry().lookupRules(arrowName, arguments[0].getClass())
					.getUnionNode();
			return executeEvaluated(arguments, ruleUnion.getRules());
		}

		protected RuleResult executeEvaluated(Object[] arguments, Rule[] rules) {
			try {
				return executeMainRules(arguments, rules);
			} catch (PatternMatchFailure pmfx) {
				return sortUnionNode.execute(arguments[0], arguments);
			}
		}

		private RuleResult executeMainRules(final Object[] arguments, Rule[] rules) {
			CompilerAsserts.compilationConstant(rules);

			for (int i = 0; i < rules.length; i++) {
				try {
					final Rule r = rules[i];
					return r.execute(Truffle.getRuntime().createVirtualFrame(arguments, r.getFrameDescriptor()));
				} catch (PatternMatchFailure pmfx) {
					if (i == rules.length) {
						throw pmfx;
					}
				}
			}

			// there are no rules. we throw a soft exception to allow sort-based rules to be tried
			throw PatternMatchFailure.INSTANCE;
		}

	}

}
