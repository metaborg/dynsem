package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public class RuleFactory {

	public static Rule createRule(DynSemLanguage lang, SourceSection source, List<? extends Rule> rules,
			String arrowName, Class<?> dispatchClass) {
		return createDeep(lang, source, rules, arrowName, dispatchClass);
		// return createShallow(lang, source, rules, arrowName, dispatchClass);
	}

	@TruffleBoundary
	public static Rule createDeep(DynSemLanguage lang, SourceSection source, List<? extends Rule> rules,
			String arrowName, Class<?> dispatchClass) {
		CompilerAsserts.neverPartOfCompilation();
		if (rules.size() == 0) {
			return new FallbackRule(lang, source, arrowName, dispatchClass);
		} else {
			Rule tail = createRule(lang, source, rules.subList(1, rules.size()), arrowName, dispatchClass);
			return RulesNodeGen.create(lang, source, rules.get(0).getCallTarget(), tail.getCallTarget());
		}
	}

	@TruffleBoundary
	public static Rule createShallow(DynSemLanguage lang, SourceSection source, List<? extends Rule> rules,
			String arrowName, Class<?> dispatchClass) {
		CompilerAsserts.neverPartOfCompilation();
		Rule[] rulez = rules.toArray(new Rule[rules.size() + 1]);
		rulez[rulez.length - 1] = new FallbackRule(lang, source, arrowName, dispatchClass);
		CallTarget[] targets = new CallTarget[rules.size() + 1];

		for (int i = 0; i < targets.length; i++) {
			targets[i] = rulez[i].getCallTarget();
		}
		return new Rules2(lang, source, targets);

	}

}
