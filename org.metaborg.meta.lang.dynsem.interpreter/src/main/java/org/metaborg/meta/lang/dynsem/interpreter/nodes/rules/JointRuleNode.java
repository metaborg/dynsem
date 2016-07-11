package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public class JointRuleNode extends DynSemNode {

	@Child private RuleUnionNode mainRuleNode;
	@Child private ASortRuleCallNode sortRuleNode;
	private String arrowName;
	private Class<?> dispatchClass;
	private RuleKind kind;

	public JointRuleNode(SourceSection source, RuleKind kind, String arrowName, Class<?> dispatchClass, Rule[] rules) {
		super(source);
		if (rules.length == 1) {
			this.mainRuleNode = new SingleRuleUnionNode(source, arrowName, dispatchClass, rules[0]);
		} else {
			this.mainRuleNode = new MultiRuleUnionNode(source, arrowName, dispatchClass, rules);
		}

		// only rules over constructors have a fallback
		if (kind == RuleKind.TERM || kind == RuleKind.ADHOC) {
			this.sortRuleNode = SortRuleCallNodeGen.create(source, arrowName);
		} else {
			this.sortRuleNode = new FailSortRuleCallNode(source);
		}

		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
		this.kind = kind;
	}

	public RuleResult execute(Object[] arguments) {
		RuleResult res = null;
		boolean repeat = true;
		while (repeat) {
			try {
				try {
					return mainRuleNode.execute(arguments);
				} catch (PatternMatchFailure pmfx) {
					return sortRuleNode.execute(arguments[0], arguments);
				}
			} catch (RecurException recex) {
				repeat = true;
			}
		}
		assert res != null;
		return res;
	}

	public RuleUnionNode getUnionNode() {
		return mainRuleNode;
	}

	public ASortRuleCallNode getSortRuleNode() {
		return sortRuleNode;
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
		return dispatchClass.getSimpleName() + "-" + arrowName + "->";
	}

}
