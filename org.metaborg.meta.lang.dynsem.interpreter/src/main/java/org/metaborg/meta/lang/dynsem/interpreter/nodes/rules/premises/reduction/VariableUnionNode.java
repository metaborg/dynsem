package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleUnionNode;

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
				return executeRules(arguments, rules);
			} catch (PremiseFailure pfx) {
				return sortUnionNode.execute(arguments[0], arguments);
			}
		}

		@ExplodeLoop
		private RuleResult executeRules(final Object[] arguments, Rule[] rules) {
			for (int i = 0; i < rules.length; i++) {
				try {
					return rules[i]
							.execute(Truffle.getRuntime().createVirtualFrame(arguments, rules[i].getFrameDescriptor()));
				} catch (PremiseFailure pfx) {
					;
				}
			}
			throw PremiseFailure.INSTANCE;
		}

	}

}
