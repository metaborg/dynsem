package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class CallAltRuleNode extends ChainedRulesNode {

	private final RuleKind parentRuleKind;
	private final String arrowName;
	private final Class<?> dispatchClass;

	public CallAltRuleNode(SourceSection source, Class<?> dispatchClass, RuleKind parentRuleKind, String arrowName) {
		super(source);
		this.dispatchClass = dispatchClass;
		this.parentRuleKind = parentRuleKind;
		this.arrowName = arrowName;
	}

	@Specialization(limit = "1", guards = "nextDispatchClass == getNextDispatchClass(reductionTerm(arguments))")
	public RuleResult doCached(Object[] arguments,
			@Cached("getNextDispatchClass(reductionTerm(arguments))") Class<?> nextDispatchClass,
			@Cached("createUnionNode(nextDispatchClass)") ChainedRulesNode targetRuleNode) {
		if (nextDispatchClass == null) {
			if (getContext().isFullBacktrackingEnabled()) {
				throw PatternMatchFailure.INSTANCE;
			} else {
				throw new ReductionFailure("No rules applicable for term " + reductionTerm(arguments),
						InterpreterUtils.createStacktrace());
			}
		}
		return targetRuleNode.execute(arguments);
	}

	@Override
	public int ruleCount() {
		return 0;
	}

	protected Object reductionTerm(Object[] arguments) {
		return arguments[0];
	}

	protected Class<?> getNextDispatchClass(Object term) {
		// decision tree based on ruleKind and dispatchClass
		Class<?> nextDispatchClass = null;
		switch (parentRuleKind) {
		case AST:
		case MAP:
		case PRIMITIVE:
		case NATIVETYPE:
			break;
		case LIST:
		case TUPLE:
			// FIXME: for now we don't fall back on AST rules for lists and tuples
			break;
		case SORT:
			nextDispatchClass = ITerm.class;
			break;
		case TERM:
			nextDispatchClass = BuiltinTypesGen.asIApplTerm(term).getSortClass();
			break;
		case PLACEHOLDER:
			if (IApplTerm.class.isAssignableFrom(dispatchClass)) {
				// we're either in a constructor or a sort position
				if (dispatchClass == BuiltinTypesGen.asIApplTerm(term).getSortClass()) {
					// we're in the sort case
					nextDispatchClass = ITerm.class;
				} else {
					// we're in the constructor case
					nextDispatchClass = BuiltinTypesGen.asIApplTerm(term).getSortClass();
				}
			} else if (ITerm.class.isAssignableFrom(dispatchClass) && dispatchClass != ITerm.class) {
				// we're in a list, tuple or something like this case
				// FIXME: for now we don't fall back on AST rules for lists and tuples
				// nextDispatchClass = ITerm.class;
			}
		}

		return nextDispatchClass;

	}

	protected ChainedRulesNode createUnionNode(Class<?> nextDispatchClass) {
		return NodeUtil
				.cloneNode(getContext().getRuleRegistry().lookupRules(arrowName, nextDispatchClass).getChainedRules());
	}

}
