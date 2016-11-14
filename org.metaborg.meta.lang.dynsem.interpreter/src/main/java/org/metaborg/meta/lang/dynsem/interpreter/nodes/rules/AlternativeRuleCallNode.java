package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class AlternativeRuleCallNode extends DynSemNode {

	private final RuleKind parentRuleKind;
	private final String arrowName;
	private final Class<?> dispatchClass;

	public AlternativeRuleCallNode(SourceSection source, Class<?> dispatchClass, RuleKind parentRuleKind,
			String arrowName) {
		super(source);
		this.dispatchClass = dispatchClass;
		this.parentRuleKind = parentRuleKind;
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Object[] arguments);

	@Specialization(limit = "1", guards = "nextDispatchClass == getNextDispatchClass(reductionTerm(arguments))")
	public RuleResult doCached(Object[] arguments,
			@Cached("getNextDispatchClass(reductionTerm(arguments))") Class<?> nextDispatchClass,
			@Cached("createUnionNode(nextDispatchClass)") JointRuleNode targetRuleNode) {
		if (nextDispatchClass == null) {
			executeFailure(reductionTerm(arguments));
		}
		return targetRuleNode.execute(arguments);
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

	private static void executeFailure(Object term) {
		if (DynSemContext.LANGUAGE.isFullBacktrackingEnabled()) {
			throw PatternMatchFailure.INSTANCE;
		} else {
			throw new ReductionFailure("No rules applicable for term " + term, InterpreterUtils.createStacktrace());
		}
	}

	protected JointRuleNode createUnionNode(Class<?> nextDispatchClass) {
		return NodeUtil
				.cloneNode(getContext().getRuleRegistry().lookupRules(arrowName, nextDispatchClass).getJointNode());
	}

}
