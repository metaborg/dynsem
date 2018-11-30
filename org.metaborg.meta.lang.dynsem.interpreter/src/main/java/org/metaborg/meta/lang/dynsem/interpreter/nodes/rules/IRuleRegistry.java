package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CallTarget;

public interface IRuleRegistry {

	DynSemLanguage getLanguage();

	public void setLanguage(DynSemLanguage language);

	void registerRule(String arrowName, Class<?> dispatchClass, RuleRootNode[] rules);

	CallTarget[] lookupCallTargets(String arrowName, Class<?> dispatchClass);

	RuleRootNode[] lookupRuleRoots(String arrowName, Class<?> dispatchClass);

	RuleRootNode lookupRuleRoot(CallTarget target);


}