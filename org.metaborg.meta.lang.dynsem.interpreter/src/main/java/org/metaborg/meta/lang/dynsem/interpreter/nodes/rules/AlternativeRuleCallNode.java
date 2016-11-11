package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public class AlternativeRuleCallNode extends AAlternativeRuleCallNode {

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

	@Override
	public RuleResult execute(Object[] arguments) {
		Class<?> nextDispatchClass = getNextDispatchClass(dispatchClass, parentRuleKind, arguments[0]);

//		if (nextDispatchClass != null) {
//			System.out.println(
//					"Executing alternative to " + dispatchClass + " on next dispatch " + nextDispatchClass.getName());
//		} else {
//			System.out.println("Failing with lack of alternatives for dispatch " + dispatchClass);
//		}

		if (nextDispatchClass == null) {
			executeFailure(arguments[0]);
		}
		JointRuleRoot root = getContext().getRuleRegistry().lookupRules(arrowName, nextDispatchClass);

		return root.execute(Truffle.getRuntime().createVirtualFrame(arguments, new FrameDescriptor()));
	}

	private static Class<?> getNextDispatchClass(Class<?> currentDispatchClass, RuleKind kind, Object term) {
		// decision tree based on ruleKind and dispatchClass
		Class<?> nextDispatchClass = null;
		switch (kind) {
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
			if (IApplTerm.class.isAssignableFrom(currentDispatchClass)) {
				// we're either in a constructor or a sort position
				if (currentDispatchClass == BuiltinTypesGen.asIApplTerm(term).getSortClass()) {
					// we're in the sort case
					nextDispatchClass = ITerm.class;
				} else {
					// we're in the constructor case
					nextDispatchClass = BuiltinTypesGen.asIApplTerm(term).getSortClass();
				}
			} else if (ITerm.class.isAssignableFrom(currentDispatchClass) && currentDispatchClass != ITerm.class) {
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

}
