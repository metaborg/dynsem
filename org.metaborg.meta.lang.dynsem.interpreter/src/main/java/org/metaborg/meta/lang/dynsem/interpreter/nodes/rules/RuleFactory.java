package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.source.SourceSection;

public class RuleFactory {

	public static CallTarget[] createRuleTargets(DynSemLanguage lang, SourceSection source, List<RuleRootNode> rules,
			String arrowName, Class<?> dispatchClass) {
		CallTarget[] targets = new CallTarget[rules.size()];
		for (int i = 0; i < targets.length; i++) {
			targets[i] = Truffle.getRuntime().createCallTarget(rules.get(i));
		}
		return targets;
	}

}
