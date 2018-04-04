package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.source.SourceSection;

public class RuleFactory {

	public static Rule createRule(DynSemLanguage lang, SourceSection source, List<? extends Rule> rules,
			String arrowName, Class<?> dispatchClass) {
		CompilerAsserts.neverPartOfCompilation();
		if (rules.size() == 0) {
			return new FallbackRule(lang, source, arrowName, dispatchClass);
		} else if (rules.size() == 1) {
			return rules.get(0);
		} else {
			Rule tail = createRule(lang, source, rules.subList(1, rules.size()), arrowName, dispatchClass);
			return new Rules(lang, source, rules.get(0), tail);
		}
	}

}
