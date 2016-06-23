package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.SortRulesUnionNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction.SortRulesUnionNodeGen;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RuleUnionNode extends DynSemNode {

	@Child private SortRulesUnionNode fallbackRulesNode;
	protected final String arrowName;
	protected final Class<?> dispatchClass;

	public RuleUnionNode(SourceSection source, String arrowName, Class<?> dispatchClass) {
		super(source);
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
		this.fallbackRulesNode = SortRulesUnionNodeGen.create(source, arrowName);
	}

	public RuleResult execute(final Object[] arguments) {
		RuleResult res = null;
		boolean repeat = true;
		while (repeat) {
			try {
				try {
					res = executeMainRule(arguments);
				} catch (PatternMatchFailure pmfx) {
					res = executeFallback(arguments);
				}
				repeat = false;
			} catch (RecurException recex) {
				repeat = true;
			}
		}
		assert res != null;
		return res;
	}

	protected abstract RuleResult executeMainRule(Object[] arguments);

	private final RuleResult executeFallback(Object[] arguments) {
		return fallbackRulesNode.execute(arguments[0], arguments);
	}
	
	public abstract List<Rule> getRules();
	
	public SortRulesUnionNode getFallbackRulesNode() {
		return fallbackRulesNode;
	}
	
	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + arrowName + "->";
	}

}
