package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public class JointRuleNode extends DynSemNode {

	@Child private RuleSetNode primaryRulesNode;

	@Child private AAlternativeRuleCallNode alternativeRulesNode;

	private final String arrowName;
	private final Class<?> dispatchClass;
	private final RuleKind kind;

	public JointRuleNode(SourceSection source, RuleKind kind, String arrowName, Class<?> dispatchClass, Rule[] rules) {
		super(source);
		this.kind = kind;
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;

		this.primaryRulesNode = new NonEmptyRuleSetNode(source, arrowName, dispatchClass, rules);
		this.alternativeRulesNode = new AlternativeRuleCallNode(source, dispatchClass, kind, arrowName);
	}

	public RuleResult execute(Object[] arguments) {
		RuleResult res = null;
		boolean repeat = true;
		while (repeat) {
			try {
				return executeRules(arguments);
			} catch (RecurException recex) {
				repeat = true;
			}
		}
		assert res != null;
		return res;
	}

	private RuleResult executeRules(Object[] arguments) {
//		System.out.println("Executing " + kind + " " + arrowName + " on "+ arguments[0].getClass().getName() + " dispatch " + dispatchClass.getName());
		try {
			return primaryRulesNode.execute(arguments);
		} catch (PatternMatchFailure pmfx) {
			return alternativeRulesNode.execute(arguments);
		}
	}

	public RuleSetNode getUnionNode() {
		return primaryRulesNode;
	}

	public AAlternativeRuleCallNode getSortRuleNode() {
		return alternativeRulesNode;
	}

	public String getArrowName() {
		return arrowName;
	}

	public Object getDispatchClass() {
		return dispatchClass;
	}

	public RuleKind getKind() {
		return kind;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + arrowName + "-> ";
	}

}
